package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.SgiDailyAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiPeriodAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.models.DailyWorkSummary;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.utils.DateRange;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SgiCalculationService {

    private final EmploymentRepository employmentRepository;
    private final ParentalLeaveRepository parentalLeaveRepository;
    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;
    private final SgiRuleService sgiRuleService;

    @Autowired
    public SgiCalculationService(EmploymentRepository employmentRepository, ParentalLeaveRepository parentalLeaveRepository, ShiftRepository shiftRepository, PersonRepository personRepository, SgiRuleService sgiRuleService) {
        this.employmentRepository = employmentRepository;
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
        this.sgiRuleService = sgiRuleService;
    }

    // Checks if employments are valid in the actual period
    private boolean isEmploymentActiveInPeriod(Employment emp, LocalDate from, LocalDate to) {

        LocalDate empStart = emp.getValidFrom();
        LocalDate empEnd = emp.getValidTo();

        //If employment is missing validTo it is active until further notice
        if(empEnd == null) {
            return !empStart.isAfter(to);
        }

        return !empStart.isAfter(to) && !empEnd.isBefore(from);
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

                boolean overnightShift =
                        !shift.getShiftStart().toLocalDate()
                                .equals(shift.getShiftEnd().toLocalDate());

                // Check if shift is a night shift and if not subtract break minutes
                if(!overnightShift) {
                    if(shift.getBreakMinutes() != null) {
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

        // Get the shifts for that day, it can be shifts starting and/or ending upon that day
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Shift> dailyShifts = shiftRepository.findOverlappingShifts(personId, dayStart, dayEnd);

        List<Shift> actualShifts = dailyShifts.stream().filter(s ->
                s.getType().equals(ShiftType.ACTUAL)).toList();

        List<Shift> baselineShifts = dailyShifts.stream().filter(s ->
                s.getType().equals(ShiftType.BASELINE)).toList();

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
        double gapHours = Math.max(0, baselineWorkHours - totalPlanned);

        // Calculate how many hours that are missing without any parental leave
        // To use for calculating recommended total extent of parental leave to apply for
        double workHoursGap = Math.max(0, baselineWorkHours - actualWorkHours);
        double rawRecommendedExtent = baselineWorkHours > 0 ?
                workHoursGap / baselineWorkHours
                : 0;

        double recommendedExtent = roundUpToNearest(rawRecommendedExtent);

        return DailyWorkSummary.builder()
                .baselineHours(baselineWorkHours)
                .actualHours(actualWorkHours)
                .leaveExtent(totalExtent)
                .leaveHours(leaveHours)
                .gapHours(gapHours)
                .recommendedExtent(recommendedExtent)
                .build();
    }

    public SgiDailyAnalysisResponse analyzeDay(String personalNumber, LocalDate date) {

        Person person = personRepository.findPersonByPersonalNumber(personalNumber);

        DayOfWeek dayOfWeek = date.getDayOfWeek(); // Get the week day to analyze

        DailyWorkSummary dailyWorkSummary = calculateDailyWorkSummary(person.getId(), date);

        double recommendedExtent = dailyWorkSummary.getRecommendedExtent();
        double totalExtent = dailyWorkSummary.getLeaveExtent();

        SgiStatus status;
        if (totalExtent > recommendedExtent) {
            status = SgiStatus.OVERCOMPENSATED;
        }
        else if (totalExtent < recommendedExtent) {
            status = SgiStatus.AT_RISK;
        }
        else {
            status = SgiStatus.PROTECTED;
        }

        return new SgiDailyAnalysisResponse(
                date,
                dayOfWeek,
                dailyWorkSummary.getBaselineHours(),
                dailyWorkSummary.getActualHours(),
                totalExtent,
                dailyWorkSummary.getGapHours(),
                recommendedExtent,
                status
        );
    }

//    public double getLeaveExtentOnDay(Long personId, LocalDate date) {
//        return findByPersonIdAndDate(personId, date)
//                .stream()
//                .mapToDouble(ParentalLeave::getExtent)
//                .sum();
//    }

    // Main method that compares planned hours and parental leave with the target on weekly basis
//    public SgiWeeklyAnalysisResponse analyzeWeek(Long personId, LocalDate dateInWeek) {
//
//        // Fetch actual week number and date for Monday and Sunday that week
//        int weeklyNumber = getWeekNumber(dateInWeek);
//        LocalDate weekStart = getWeekStart(dateInWeek);
//        LocalDate weekEnd = getWeekEnd(dateInWeek);
//
//        // Fetch all shifts that a person has saved in db
//        List<Shift> weeklyShifts = getShiftsForPersonInPeriod(personId, weekStart, weekEnd);
//
//        List<Shift> actualShifts = weeklyShifts.stream().filter(s ->
//                s.getType().equals(ShiftType.ACTUAL)).toList();
//
//        List<Shift> baselineShifts = weeklyShifts.stream().filter(s ->
//                s.getType().equals(ShiftType.BASELINE)).toList();
//
//        double actualWorkHours = summarizeWorkHoursInPeriod(actualShifts, weekStart, weekEnd);
//        double baselineWorkHours = summarizeWorkHoursInPeriod(baselineShifts, weekStart, weekEnd);
////        long baselineDays = baselineShifts.stream()
////                .map(sgiRuleService::identifyMainDay)
////                .distinct()
////                .count();
//        long baselineDays = baselineShifts.size();
//
//        // Fetch the actual Parental leaves that a person has planned to apply for and summarize the hours
//        List<ParentalLeave> parentalLeaves = getParentalLeaves(personId, weekStart, weekEnd);
//        double leaveHours = summarizeLeaveHoursInPeriod(parentalLeaves, baselineWorkHours, baselineDays);
//
//        double totalPlanned = actualWorkHours + leaveHours;
//        double gapHours = Math.max(0, baselineWorkHours - totalPlanned);
//
//        SgiStatus status = (gapHours <= 0)
//                ? SgiStatus.PROTECTED
//                : SgiStatus.AT_RISK;
//
//        double hoursPerDay = baselineDays > 0
//                ? baselineWorkHours / baselineDays
//                : 0;
//
//        double rawRecommendedDays = hoursPerDay > 0
//                ? gapHours / hoursPerDay
//                : 0;
//
//        double recommendedDays = roundUpToNearest(rawRecommendedDays);
//
//        // Create warnings to return for the user that is based on planned parental leave
//        // the weekend rule
//        // the exception rule for minimum 5 days free
//        String warning = collectWeeklyWarnings(personId, parentalLeaves, weeklyShifts, weekStart, weekEnd);
//
//
//        String recommendation = (status == SgiStatus.PROTECTED)
//                ? "SGI protected"
//                : "SGI at risk. You need approximately " + recommendedDays + " parental leave days.";
//
//        return new SgiWeeklyAnalysisResponse(
//                weeklyNumber,
//                actualWorkHours,
//                leaveHours,
//                totalPlanned,
//                gapHours,
//                recommendedDays,
//                status,
//                recommendation,
//                warning
//        );
//    }

// This is what analyzeweek should return
//    private int weeklyNumber;
//    private Map<DayOfWeek, SgiStatus> dailyStatus; // Showing weekday and if that day is protected, at_risk or overcompensated
//    private Map<DayOfWeek, Double> dailyRecommendation;

//    public SgiPeriodAnalysisResponse analyzePeriod(Long personId, LocalDate from, LocalDate to) {
//        List<SgiWeeklyAnalysisResponse> weeklyResults = new ArrayList<>();
//
//        // Adjust from and to so that the calculation is performed on whole weeks within the period
//        LocalDate adjustedFrom = from.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//        LocalDate adjustedTo = to.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
//
//        while(!adjustedFrom.isAfter(adjustedTo.minusDays(6))) {
//            weeklyResults.add(analyzeWeek(personId, adjustedFrom));
//            adjustedFrom = adjustedFrom.plusWeeks(1);
//        }
//
//        double totalPlanned = weeklyResults.stream().mapToDouble(SgiWeeklyAnalysisResponse::getTotalPlannedHours).sum();
//        double totalTarget = weeklyResults.stream().mapToDouble(SgiWeeklyAnalysisResponse::getTargetHours).sum();
//        totalPlanned = Math.round(totalPlanned * 100.0) / 100.0;
//        totalTarget = Math.round(totalTarget * 100.0) / 100.0;
//
//        SgiStatus overallStatus = (totalPlanned >= totalTarget) ? SgiStatus.PROTECTED : SgiStatus.AT_RISK;
//
//        String recommendation = (overallStatus == SgiStatus.PROTECTED) ? "Your total plan looks safe" :
//                "The analyze is covering " + weeklyResults.size() + " whole weeks. Total goal for these weeks" +
//                " are " + totalTarget + " hours.";
//
//        return new SgiPeriodAnalysisResponse(
//                weeklyResults,
//                totalPlanned,
//                totalTarget,
//                overallStatus,
//                recommendation
//        );
//    }

    private static double roundUpToNearest(double days) {
        if(days <= 0) {
            return 0;
        }
        double step = 0.125;
        return Math.ceil(days / step) * step;
    }

    private static int getWeekNumber(LocalDate date) {
        return date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }

    public List<ParentalLeave> getParentalLeaves(Long personId, LocalDate from, LocalDate to) {

        return parentalLeaveRepository.findByPersonIdAndDateBetween(personId, from, to);
    }

//    private String collectWeeklyWarnings(Long personId,
//                                         List<ParentalLeave> weeklyLeaves,
//                                         List<Shift> weeklyShifts,
//                                         LocalDate weekStart,
//                                         LocalDate weekEnd) {
//
//        StringBuilder warnings = new StringBuilder();
//
//        // Extend the list of shifts so that validateParentalLeaveDay looks at a wider period than a week
//        // Otherwise it might miss shifts in the week before and assume it is free days
//        List<Shift> extendedListOfShifts = getShiftsForPersonInPeriod(
//                personId,
//                weekStart.minusDays(4),
//                weekEnd.plusDays(4));
//
//        for (ParentalLeave parentalLeave : weeklyLeaves) {
//
//            List<ParentalLeave> sameDayLeaves = weeklyLeaves.stream()
//                    .filter(leaves -> leaves.getDate().equals(parentalLeave.getDate())).toList();
//
//            // Fetch parental leave extent for surrounding days
//            double fridayExtent = parentalLeaveRepository.getLeaveExtentOnDay(personId, weekStart.with(DayOfWeek.FRIDAY));
//            double mondayExtent = parentalLeaveRepository.getLeaveExtentOnDay(personId, weekEnd.plusDays(1));
//
//
//            String dayWarning = sgiRuleService.validateParentalLeave(
//                    parentalLeave,
//                    extendedListOfShifts,
//                    sameDayLeaves,
//                    fridayExtent,
//                    mondayExtent);
//
//            if (dayWarning != null) {
//                warnings.append(dayWarning).append(" ");
//            }
//        }
//        return warnings.toString().trim();
//    }

    private static LocalDate getWeekStart(LocalDate dateInWeek) {
        return dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private static LocalDate getWeekEnd(LocalDate dateInWeek) {
        return dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    // Method that can list shifts a certain period of time
    // It also looks at shifts that can overlap a period by starting before the period but ending inside the period.
    public List<Shift> getShiftsForPersonInPeriod(Long personId, LocalDate from, LocalDate to) {

        DateRange range = DateRange.of(from, to);
        return shiftRepository.findOverlappingShifts(
                personId,
                range.start(),
                range.end()
        );
    }
            /*
        public double calculateRecommendedExtent(Long personId, LocalDate date) {
         // Calculate how many hours that are missing without any parental leave
        // To use for calculating recommended total extent of parental leave to apply for

            double workHoursGap = Math.max(0, baselineWorkHours - actualWorkHours);
            double rawRecommendedExtent = baselineWorkHours > 0 ?
                workHoursGap / baselineWorkHours
                : 0;

            double recommendedExtent = roundUpToNearest(rawRecommendedExtent);

            return recommendedExtent;
         */

}
