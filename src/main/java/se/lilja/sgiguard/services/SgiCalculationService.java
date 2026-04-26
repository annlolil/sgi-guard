package se.lilja.sgiguard.services;

import org.hibernate.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.dtos.SgiPeriodAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
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

import static java.util.Arrays.stream;

@Service
public class SgiCalculationService {

    private final ShiftRepository shiftRepository;
    private final EmploymentRepository employmentRepository;
    private final ParentalLeaveRepository parentalLeaveRepository;
    private final SgiRuleService sgiRuleService;
    private final ParentalLeaveService parentalLeaveService;

    @Autowired
    public SgiCalculationService(ShiftRepository shiftRepository, EmploymentRepository employmentRepository, ParentalLeaveRepository parentalLeaveRepository, SgiRuleService sgiRuleService, ParentalLeaveService parentalLeaveService) {
        this.shiftRepository = shiftRepository;
        this.employmentRepository = employmentRepository;
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.sgiRuleService = sgiRuleService;
        this.parentalLeaveService = parentalLeaveService;
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

    // Calculates the target hours to work for all employments if a person has more than one
    // Filters on employments that are valid in the chosen period.
    public Double calculateTotalWeeklyTarget(Long personId, LocalDate from, LocalDate to) {

        List<Employment> employments = employmentRepository.findByPersonId(personId);

        if (from == null) {
            throw new IllegalArgumentException("from not be null");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to must not be before from");
        }

        double totalTarget = employments.stream()
                .filter(emp -> isEmploymentActiveInPeriod(emp, from, to))
                .mapToDouble(this::calculateOriginalTarget)
                .sum();

        return Math.round(totalTarget * 100.0) / 100.0;
    }

    // Method that summarizes the hours from all shifts listed in a specific period of time
    // It can be for example a month, 4 weeks or 6 weeks
    public Double summarizeWorkHoursInPeriod(List<Shift> shifts, LocalDate from, LocalDate to) {

        double totalMinutes = 0;

        for (Shift shift : shifts) {
            // Only count the hours for a shift if its main day is in the period
            LocalDate mainDay = sgiRuleService.identifyMainDay(shift);
            if (!mainDay.isBefore(from) && !mainDay.isAfter(to)) {
                Duration duration = Duration.between(shift.getShiftStart(), shift.getShiftEnd());
                totalMinutes += duration.toMinutes();
            }
        }

        double totalHours = totalMinutes / 60.0;

        return Math.round(totalHours * 100.0) / 100.0;
    }

    // Method that summarizes the hours from all parental leave listed in a specific period of time
    // It can be for example a month, 4 weeks or 6 weeks
    public Double summarizeLeaveHoursInPeriod(List<ParentalLeave> parentalLeaves, Double weeklyTarget) {

        double hoursPerDay = weeklyTarget / 5.0;

        double totalParentalLeaveHours = parentalLeaves.stream()
                .mapToDouble(leave -> leave.getExtent() * hoursPerDay).sum();

        return Math.round(totalParentalLeaveHours * 100.0) / 100.0;
    }

    // Main method that compares planned hours and parental leave with the target on weekly basis
    public SgiWeeklyAnalysisResponse analyzeWeek(Long personId, LocalDate dateInWeek) {

        int weeklyNumber = getWeekNumber(dateInWeek);

        // Create a week from the input dateInWeek
        LocalDate weekStart = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        double targetHours = calculateTotalWeeklyTarget(personId, weekStart, weekEnd);

        List<Shift> weeklyShifts = getShiftsForPersonInPeriod(personId, weekStart, weekEnd);
        double workHours = summarizeWorkHoursInPeriod(weeklyShifts, weekStart, weekEnd);

        List<ParentalLeave> weeklyLeaves = getParentalLeaves(personId, weekStart, weekEnd);
        double leaveHours = summarizeLeaveHoursInPeriod(weeklyLeaves, targetHours);

        double totalPlanned = workHours + leaveHours;

        double gapHours = calculateGap(targetHours, totalPlanned);
        double recommendedDays = calculateRecommendedDaysToClaim(gapHours, targetHours);

        SgiStatus status = (totalPlanned >= targetHours) ? SgiStatus.PROTECTED : SgiStatus.AT_RISK;

        StringBuilder warning = new StringBuilder();

        LocalDate friday = weekStart.with(DayOfWeek.FRIDAY);
        LocalDate monday = weekEnd.plusDays(1);

        double fridayLeaveExtent = parentalLeaveService.getLeaveExtentOnDay(personId, friday);
        double mondayLeaveExtent = parentalLeaveService.getLeaveExtentOnDay(personId, monday);

        for(ParentalLeave parentalLeave : weeklyLeaves) {

            // Check if a person is off work for at least 5 days
            boolean isLongLeave = sgiRuleService.is5DayFree(parentalLeave.getDate(),weeklyShifts);
            boolean validParentalLeave;
            if(isLongLeave) {
                validParentalLeave = true;
                // Else check if parental leave is valid according to weekend rules
            } else {
                validParentalLeave = sgiRuleService.isWeekendClaimValid(
                        parentalLeave.getDate(),
                        fridayLeaveExtent,
                        mondayLeaveExtent,
                        parentalLeave.getExtent());
            }
            if(!validParentalLeave) {
                warning.append("Warning: Claim on ")
                        .append(parentalLeave.getDate())
                        .append(" requires at least the same extent ")
                        .append("on connecting weekday");
            }
        }

        String recommendation = (status == SgiStatus.PROTECTED)
                ? "SGI protected"
                : "SGI at risk in week " + getWeekNumber(weekStart) + ". You are missing " + recommendedDays + " days.";

        return new SgiWeeklyAnalysisResponse(
                weeklyNumber,
                workHours,
                leaveHours,
                totalPlanned,
                targetHours,
                gapHours,
                recommendedDays,
                status,
                recommendation,
                warning
        );
    }

    public double calculateRecommendedDaysToClaim(double gapHours, double weeklyTarget) {

        if(gapHours <= 0 || weeklyTarget <= 0) {
            return 0.0;
        }

        // This is based on the assumption that a day of SGI is always 1/5 of the weekly target
        double hoursPerDay = weeklyTarget / 5.0;
        double daysMissing = gapHours / hoursPerDay;
        double sgiDaysMissing = roundUpToNearest(daysMissing);

        return Math.round(sgiDaysMissing * 1000.0) / 1000.0; //Return the nearest number of SGI days that is missing.
    }

    public SgiPeriodAnalysisResponse analyzePeriod(Long personId, LocalDate from, LocalDate to) {
        List<SgiWeeklyAnalysisResponse> weeklyResults = new ArrayList<>();

        // Adjust from and to so that the calculation is performed on whole weeks within the period
        LocalDate adjustedFrom = from.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate adjustedTo = to.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        while(!adjustedFrom.isAfter(adjustedTo.minusDays(6))) {
            weeklyResults.add(analyzeWeek(personId, adjustedFrom));
            adjustedFrom = adjustedFrom.plusWeeks(1);
        }

        double totalPlanned = weeklyResults.stream().mapToDouble(SgiWeeklyAnalysisResponse::getTotalPlannedHours).sum();
        double totalTarget = weeklyResults.stream().mapToDouble(SgiWeeklyAnalysisResponse::getTargetHours).sum();
        totalPlanned = Math.round(totalPlanned * 100.0) / 100.0;
        totalTarget = Math.round(totalTarget * 100.0) / 100.0;

        SgiStatus overallStatus = (totalPlanned >= totalTarget) ? SgiStatus.PROTECTED : SgiStatus.AT_RISK;

        String recommendation = (overallStatus == SgiStatus.PROTECTED) ? "Your total plan looks safe" :
                "The analyze is covering " + weeklyResults.size() + " whole weeks. Total goal for these weeks" +
                " are " + totalTarget + " hours.";

        return new SgiPeriodAnalysisResponse(
                weeklyResults,
                totalPlanned,
                totalTarget,
                overallStatus,
                recommendation
        );
    }

    // Helpers
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

    private static double calculateGap(double target, double planned){
        double gap = Math.max(0, target - planned);
        return Math.round(gap * 100.0) / 100.0;
    }

    public List<ParentalLeave> getParentalLeaves(Long personId, LocalDate from, LocalDate to) {

        return parentalLeaveRepository.findByPersonIdAndDateBetween(personId, from, to);
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

    // Calculates the target hours to work (the SGI could be decided on a lower percentage)
    private double calculateOriginalTarget(Employment employment) {

        return (employment.getOriginalWorkingHours() * employment.getOriginalEmploymentRate()) / 100.0;
    }
}
