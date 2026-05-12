package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SgiRuleService {

    private final ShiftRepository shiftRepository;

    @Autowired
    public SgiRuleService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    // A method that takes a shift and identifies which day is the main day of working hours
//    public LocalDate identifyMainDay(Shift shift) {
//
//        LocalDateTime start = shift.getShiftStart();
//        LocalDateTime end = shift.getShiftEnd();
//
//        if (start.toLocalDate().isEqual(end.toLocalDate())) {
//            return start.toLocalDate();
//        }
//
//        LocalDateTime midnight = start.toLocalDate().plusDays(1).atStartOfDay();
//
//        long minutesFirstDay = Duration.between(start, midnight).toMinutes();
//        long minutesSecondDay = Duration.between(midnight, end).toMinutes();
//
//        if (minutesFirstDay >= minutesSecondDay) {
//            return start.toLocalDate();
//        }
//        else {
//            return end.toLocalDate();
//        }
//    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    // Check if saturday or sunday extent is more or the same as monday or friday extent
    // according to weekend rules
    public boolean isWeekendExtentValid(double fridayExtent, double mondayExtent, double currentExtent) {

        return fridayExtent >= currentExtent || mondayExtent >= currentExtent;
    }

    public boolean isWorkFreeDay(Long personId, LocalDate date) {

        List<Shift> shifts =
                shiftRepository.findOverlappingShifts(
                        personId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                );

        boolean hasBaselineShift =
                shifts.stream()
                        .anyMatch(s -> s.getType() == ShiftType.BASELINE);

        return !hasBaselineShift;
    }

    // Checks if a person has >=5 free days of work
    // According to Forsakringskassan it is okay to apply for parental leave on a saturday, sunday
    // or other day a person would not work as long as it is surrounded by 4 other free days.
    public boolean is5DayFree(LocalDate date, Long personId) {

        int totalFreeDays = 0;

        for (int i = -4; i <= 4; i++) {

            LocalDate currentDate = date.plusDays(i);

            boolean freeDays = isWorkFreeDay(personId, currentDate);

            if (freeDays) {
                totalFreeDays++;
            } else {
                totalFreeDays = 0; // Resets totalFreeDays if the 5-day period breaks with a work day
            }

            if (totalFreeDays >= 5) {
                    return true;
            }
        }
        return false;
    }

    // Check if total extent for a day exceeds the maximum value of 1.0 days
    public boolean isExtentValid(ParentalLeave newLeave, double existingExtent) {

        double totalExtent = existingExtent + newLeave.getExtent();

        return !(totalExtent > 1.0);
    }

//    public List<String> getParentalLeaveWarnings(
//            ParentalLeave parentalLeave,
//            List<Shift> shifts,
//            List<ParentalLeave> sameDayLeaves,
//            double fridayExtent,
//            double mondayExtent) {
//
//        List<String> warnings = new ArrayList<>();
//
//        if(!isExtentValid(parentalLeave, sameDayLeaves)) {
//            warnings.add("Parental leave exceeds 100% on one day");
//        }
//
//        if(!validateWeekendRule(parentalLeave, shifts, fridayExtent, mondayExtent)) {
//
//                warnings.add("Weekend rule may not be fulfilled");
//            }
//        return warnings;
//    }
}
