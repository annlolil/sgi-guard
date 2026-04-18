package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.dtos.SgiStatusResponse;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.utils.DateRange;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SgiCalculationService {

    private final ShiftRepository shiftRepository;
    private final EmploymentRepository employmentRepository;

    @Autowired
    public SgiCalculationService(ShiftRepository shiftRepository, EmploymentRepository employmentRepository) {
        this.shiftRepository = shiftRepository;
        this.employmentRepository = employmentRepository;
    }

    // A method that takes a shift and identifies which day is the main day of working hours
    public LocalDate identifyMainDay(Shift shift) {

        LocalDateTime start = shift.getShiftStart();
        LocalDateTime end = shift.getShiftEnd();

        if (start.toLocalDate().isEqual(end.toLocalDate())) {
            return start.toLocalDate();
        }

        LocalDateTime midnight = start.toLocalDate().plusDays(1).atStartOfDay();

        long minutesFirstDay = Duration.between(start, midnight).toMinutes();
        long minutesSecondDay = Duration.between(midnight, end).toMinutes();

        if (minutesFirstDay >= minutesSecondDay) {
            return start.toLocalDate();
        }
        else {
            return end.toLocalDate();
        }
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

    // Calculates the target hours to work based on ONE employment
    private double calculateCurrentHours(Employment employment) {
        return (employment.getOriginalWorkingHours() * employment.getCurrentEmploymentRate()) / 100.0;
    }

    // Checks if employments are valid in the period that sgi calculation is being performed
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
                .mapToDouble(this::calculateCurrentHours)
                .sum();

        return Math.round(totalTarget * 100.0) / 100.0;
    }

    // Method that summarizes the hours from all shifts listed in a specific period of time
    // It can be for example a month, 4 weeks or 6 weeks
    public Double summarizePlannedHoursInPeriod(Long personId, LocalDate from, LocalDate to) {

        List<Shift> shiftsInPeriod = getShiftsForPersonInPeriod(personId, from, to);

        double totalMinutes = 0;

        for (Shift shift : shiftsInPeriod) {
            // Only count the hours for a shift if its main day is in the period
            LocalDate mainDay = identifyMainDay(shift);
            if (!mainDay.isBefore(from) && !mainDay.isAfter(to)) {
                Duration duration = Duration.between(shift.getShiftStart(), shift.getShiftEnd());
                totalMinutes += duration.toMinutes();
            }
        }

        double totalHours = totalMinutes / 60.0;

        return Math.round(totalHours * 100.0) / 100.0;
    }

    private double calculateGap(double target, double planned){

        double gap = Math.max(0, target - planned);
        return Math.round(gap * 100.0) / 100.0;
    }

    // Main method that compares planned hours to work with the target and gives a recommendation
    public SgiStatusResponse calculateSgiStatus(Long personId, LocalDate from, LocalDate to) {

        if(from == null) {
            throw new IllegalArgumentException("From is null");
        }
        if(to.isBefore(from)) {
            throw new IllegalArgumentException("To is before from");
        }

        long daysInPeriod = ChronoUnit.DAYS.between(from, to) + 1;
        double weeksInPeriod = daysInPeriod / 7.0;

        double plannedHours = summarizePlannedHoursInPeriod(personId, from, to);

        double weeklyTargetHours = calculateTotalWeeklyTarget(personId, from, to);
        double totalTargetForPeriod = Math.round((weeklyTargetHours * weeksInPeriod) * 100.0) / 100.0;
        double gapHours = calculateGap(totalTargetForPeriod, plannedHours);
        double recommendedDays = calculateRecommendedDaysToClaim(personId, gapHours);

        return getResponse(plannedHours, totalTargetForPeriod, gapHours, recommendedDays);
    }

    private static SgiStatusResponse getResponse(
            double plannedHours, double totalTargetForPeriod, double gapHours, double recommendedDays) {

        SgiStatus sgiStatus = (plannedHours >= totalTargetForPeriod) ? SgiStatus.PROTECTED : SgiStatus.AT_RISK;

        String recommendation = sgiStatus == SgiStatus.PROTECTED ?
                "Protected SGI" : "You need to fill up with around " + recommendedDays + " of parental leave";

        SgiStatusResponse sgiStatusResponse = new SgiStatusResponse();
        sgiStatusResponse.setPlannedHours(plannedHours);
        sgiStatusResponse.setTargetHours(totalTargetForPeriod);
        sgiStatusResponse.setGapHours(gapHours);
        sgiStatusResponse.setStatus(sgiStatus);
        sgiStatusResponse.setRecommendation(recommendation);
        sgiStatusResponse.setRecommendedDays(recommendedDays);
        return sgiStatusResponse;
    }

    private Double calculateRecommendedDaysToClaim(Long personId, double gapHours) {

        if(gapHours <= 0) {
            return 0.0;
        }
        // Get a persons summarized original working hours per week
        List<Employment> employments = employmentRepository.findByPersonId(personId);
        double fullTimeWeeklyHours = employments.stream()
                .mapToDouble(Employment::getOriginalWorkingHours)
                .sum();

        // Calculate what one day corresponds to
        double hoursPerDay = fullTimeWeeklyHours / 5.0;

        double daysMissing = gapHours / hoursPerDay;

        return Math.round(daysMissing * 100.0) / 100.0; //Return the nearest parental benefit days later!

    }
}
