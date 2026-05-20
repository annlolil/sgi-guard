package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.SgiDailyAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.models.DailyWorkSummary;
import se.lilja.sgiguard.models.LeaveExtent;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.utils.DateRange;

import java.sql.SQLOutput;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SgiCalculationService {

    private final ParentalLeaveRepository parentalLeaveRepository;
    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SgiCalculationService(ParentalLeaveRepository parentalLeaveRepository, ShiftRepository shiftRepository, PersonRepository personRepository) {
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
    }

    public double summarizeWorkHoursForDay(List<Shift> shifts, LocalDate date) {

        double totalMinutes = 0;

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        for (Shift shift : shifts) {

            LocalDateTime shiftStart = shift.getShiftStart();
            LocalDateTime shiftEnd = shift.getShiftEnd();

            LocalDateTime overlapStart =
                    shiftStart.isAfter(dayStart)
                            ? shiftStart
                            : dayStart;

            LocalDateTime overlapEnd =
                    shiftEnd.isBefore(dayEnd)
                            ? shiftEnd
                            : dayEnd;

            if (overlapStart.isBefore(overlapEnd)) {

                Duration duration =
                        Duration.between(overlapStart, overlapEnd);

                totalMinutes += duration.toMinutes();

                // Check if shift is a night shift and if not subtract break minutes
                if (!overnightShift(shift)) {
                    if (shift.getBreakMinutes() != null) {
                        totalMinutes -= shift.getBreakMinutes();
                    }
                }
            }
        }
        return Math.round((totalMinutes / 60.0) * 100.0) / 100.0;
    }

    public DailyWorkSummary calculateDailyWorkSummary(Long personId, LocalDate date) {
        /*fetch shifts
        summarize baseline
        summarize actual
        calculate gap
        calculate recommended extent
        return DailyWorkSummary.*/

        // Get the shifts for that day, it can be shifts starting and/or ending inside that day
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Shift> dailyShifts = shiftRepository.findOverlappingShifts(personId, dayStart, dayEnd);

        List<Shift> actualShifts = dailyShifts.stream().filter(s ->
                s.getType() == ShiftType.ACTUAL).toList();

        List<Shift> baselineShifts = dailyShifts.stream().filter(s ->
                s.getType() == ShiftType.BASELINE).toList();

        double actualWorkHours = summarizeWorkHoursForDay(actualShifts, date);
        double baselineWorkHours = summarizeWorkHoursForDay(baselineShifts, date);

        // Get existing parental leaves for this day
        List<ParentalLeave> parentalLeaves = parentalLeaveRepository.findByPersonIdAndDate(personId, date);
        double totalExtent = parentalLeaves.stream().mapToDouble(ParentalLeave::getExtent).sum();
        double leaveHours = baselineWorkHours > 0 ?
                totalExtent * baselineWorkHours
                : 0;

        // Calculate how many hours that are missing including existing parental leave
        double totalPlanned = actualWorkHours + leaveHours;

        // Calculate how many hours that are missing without any parental leave
        // To use for calculating recommended total extent of parental leave to apply for
        double requiredLeaveHours = Math.max(0, baselineWorkHours - actualWorkHours);

        double recommendedExtent = calculateRecommendedExtent(date, baselineShifts, actualShifts);
        double remainingGapHours = 0;
        if(totalExtent < recommendedExtent && baselineWorkHours >= 1.0) {
            double rawRemainingGapHours = Math.max(0, baselineWorkHours - totalPlanned);
            remainingGapHours = Math.round(rawRemainingGapHours * 100.0) / 100.0;
        }

        return DailyWorkSummary.builder()
                .baselineHours(baselineWorkHours)
                .actualHours(actualWorkHours)
                .leaveExtent(totalExtent)
                .leaveHours(leaveHours)
                .remainingGapHours(remainingGapHours)
                .requiredLeaveHours(requiredLeaveHours)
                .recommendedExtent(recommendedExtent)
                .build();
    }

    public SgiDailyAnalysisResponse analyzeDay(String personalNumber, LocalDate date) {

        Person person = personRepository.findPersonByPersonalNumber(personalNumber)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        // Get the week day to analyze (just for visual purposes)
        String dayOfWeek = date.getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.of("sv", "SE"));
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase()
                        + dayOfWeek.substring(1);

        DailyWorkSummary dailyWorkSummary = calculateDailyWorkSummary(person.getId(), date);

        double recommendedExtent = dailyWorkSummary.getRecommendedExtent();
        double totalExtent = dailyWorkSummary.getLeaveExtent();

        // Set the SGI status
        double remainingGapHours = dailyWorkSummary.getRemainingGapHours();

        SgiStatus status;
        if (totalExtent > recommendedExtent
                && remainingGapHours == 0) {

            status = SgiStatus.OVERCOMPENSATED;

        } else if (remainingGapHours > 0) {

            status = SgiStatus.AT_RISK;

        } else {

            status = SgiStatus.PROTECTED;
        }

        // Format hours for UI
        String formattedBaseLineWorkHours = formatHoursAndMinutes(dailyWorkSummary.getBaselineHours());
        String formattedActualWorkHours = formatHoursAndMinutes(dailyWorkSummary.getActualHours());
        String formattedRemainingGapHours = formatHoursAndMinutes(dailyWorkSummary.getRemainingGapHours());

        // Format extent label for UI
        String recommendedExtentLabel = getExtentLabel(recommendedExtent);
        String totalExtentLabel = getExtentLabel(totalExtent);

        return new SgiDailyAnalysisResponse(
                date,
                dayOfWeek,
                formattedBaseLineWorkHours,
                formattedActualWorkHours,
                totalExtentLabel,
                formattedRemainingGapHours,
                recommendedExtent,
                recommendedExtentLabel,
                status
        );
    }

    // Creates a collection of dates for a whole week
    private static LocalDate[] getWeekDates(LocalDate weekStart) {

        LocalDate[] daysInWeek = new LocalDate[7];

        for (int i = 0; i < 7; i++) {
            daysInWeek[i] = weekStart;
            weekStart = weekStart.plusDays(1);
        }
        return daysInWeek;
    }

    // Returns a weekly result of sgi statuses using analyze day
    public SgiWeeklyAnalysisResponse analyzeWeek(String personalNumber, LocalDate date) {

        Person person = personRepository.findPersonByPersonalNumber(personalNumber)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        // Fetch actual week number and date for Monday and Sunday that week
        int weeklyNumber = getWeekNumber(date);
        LocalDate weekStart = getWeekStart(date);

        LocalDate[] daysInWeek = getWeekDates(weekStart);
        List<SgiDailyAnalysisResponse> dailyResponses = new ArrayList<>();

        for (LocalDate localDate : daysInWeek) {
            dailyResponses.add(analyzeDay(person.getPersonalNumber(), localDate));
        }

        return new SgiWeeklyAnalysisResponse(
                weeklyNumber,
                dailyResponses
        );
    }

    private double roundUpToNearestValidExtent(double extent) {

        if (extent <= 0) {
            return 0;
        }

        for (LeaveExtent valid : LeaveExtent.values()) {

            if (extent <= valid.getValue()) {
                return valid.getValue();
            }
        }

        return 1.0;
    }

    private static int getWeekNumber(LocalDate date) {
        return date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }

    private static LocalDate getWeekStart(LocalDate dateInWeek) {
        return dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public double calculateRecommendedExtent(LocalDate date, List<Shift> baseLineShifts, List<Shift> actualShifts) {
        // Calculate how many hours that are missing without any parental leave
        // To use for calculating recommended total extent of parental leave to apply for
        // Looks at both day shifts and overlapping shifts

        double rawRecommendedExtent = 0.0;

        Map<Shift, List<Shift>> belongingShifts = findBelongingShifts(baseLineShifts, actualShifts);

        for (var entry : belongingShifts.entrySet()) {

            Shift baselineShift = entry.getKey();

            List<Shift> matchingActuals =
                    entry.getValue();

            // Whole shift duration
            double totalShiftHours = Duration.between(
                    baselineShift.getShiftStart(), baselineShift.getShiftEnd()).toMinutes() / 60.0;

            // Baseline hours for THIS DAY only
            double baselineHoursForDay = summarizeWorkHoursForDay(
                    List.of(baselineShift), date);

            // Actual worked hours for THIS DAY only
            double actualHoursForDay = summarizeWorkHoursForDay(
                    matchingActuals, date);

            // Missing hours for this shift/day
            double missingHours = Math.max(0, baselineHoursForDay - actualHoursForDay);

            // Calculating extent depending on if the shift is a nightshift or not
            // If nightshift calculate using the whole shifts hours
            // If dayshift calculate using the days (planned) total baselinehours

            double denominator;
            if(overnightShift(baselineShift)){
                denominator = totalShiftHours;
            }
            else{
                denominator = baselineHoursForDay;
            }
            // Extent contribution
            rawRecommendedExtent += missingHours / denominator;
        }

        return roundUpToNearestValidExtent(
                rawRecommendedExtent);
    }

    private Map<Shift, List<Shift>> findBelongingShifts(List<Shift> baseLineShifts, List<Shift> actualShifts) {

        Map<Shift, List<Shift>> belongingShifts = new HashMap<>();

        for(Shift baseLineShift : baseLineShifts) {
            List<Shift> matchingActuals = actualShifts.stream()
                    .filter(actualShift -> shiftsOverlap(baseLineShift, actualShift)).toList();

            belongingShifts.put(baseLineShift, matchingActuals);
        }
        return belongingShifts;
    }

    private boolean shiftsOverlap(
            Shift shift1,
            Shift shift2) {

        return shift1.getShiftStart()
                .isBefore(shift2.getShiftEnd())
                &&
                shift1.getShiftEnd()
                        .isAfter(shift2.getShiftStart());
    }

    // Used for UI, thymeleaf
    public String getExtentLabel(double extent) {

        String extentLabel;

        if (extent == 0.125) {
            extentLabel = "1/8 dag";
        } else if (extent == 0.25) {
            extentLabel = "1/4 dag";
        } else if (extent == 0.5) {
            extentLabel = "1/2 dag";
        } else if (extent == 0.75) {
            extentLabel = "3/4 dag";
        } else if (extent == 1.0) {
            extentLabel = "Hel dag";
        } else if (extent == 0.0) {
            extentLabel = "0 dagar";
        }
        else {
            extentLabel = String.valueOf(extent);
        }
        return extentLabel;
    }

    private boolean overnightShift(Shift shift) {
        return !shift.getShiftStart().toLocalDate()
                .equals(shift.getShiftEnd().toLocalDate());
    }

    // For UI to show hours in 3 h 10 min instead of 3,17
    public String formatHoursAndMinutes(double hours) {

        int totalMinutes = (int)Math.round(hours * 60);

        int wholeHours = totalMinutes / 60;
        int remainingMinutes = totalMinutes % 60;

        if (remainingMinutes == 0) {
            return wholeHours + " h";
        }

        return wholeHours + " h " + remainingMinutes + " min";
    }
}
