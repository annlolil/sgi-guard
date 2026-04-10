package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.utils.DateRange;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SgiCalculationService {

    private final ShiftRepository shiftRepository;

    @Autowired
    public SgiCalculationService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
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

    //Method that calculates the hours that a person should work in average per week
    // considering the persons workconditions
    public Double calculateCurrentWeeklyHours(Employment employment) {

        double target = employment.getOriginalWorkingHours() * employment.getCurrentEmploymentRate()/100.0;

        return Math.round(target*100.0)/100.0;
    }

    // Method that summarizes the hours from all shifts listed in a specific period of time
    public Double summarizeWorkHoursInPeriod(Long personId, LocalDate from, LocalDate to) {

        List<Shift> shiftsInPeriod = getShiftsForPersonInPeriod(personId, from, to);

        double totalMinutes = 0;

        for (Shift shift : shiftsInPeriod) {
            // Only count the hours for a shift if its mainday is in the period
            LocalDate mainDay = identifyMainDay(shift);
            if (!mainDay.isBefore(from) && !mainDay.isAfter(to)) {
                Duration duration = Duration.between(shift.getShiftStart(), shift.getShiftEnd());
                totalMinutes += duration.toMinutes();
            }
        }

        double totalHours = totalMinutes / 60.0;

        return Math.round(totalHours * 100.0) / 100.0;
    }
}
