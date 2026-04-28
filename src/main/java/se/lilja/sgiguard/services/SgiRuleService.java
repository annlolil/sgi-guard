package se.lilja.sgiguard.services;

import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Shift;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SgiRuleService {

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

    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public boolean isWeekendClaimValid(LocalDate weekendDate, double fridayExtent, double mondayExtent, double currentExtent) {
        // Not a weekend so the rule do not apply
        if(!isWeekend(weekendDate)) return true;

        if(weekendDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return fridayExtent >= currentExtent;
        }
        else {
            return mondayExtent >= currentExtent;
        }
    }

    // Checks if a person has 5 free days of work
    // According to Forsakringskassan it is okay to apply for parental leave on a saturday, sunday
    // or other day a person would not work as long as it is surrounded by 4 other free days.
    public boolean is5DayFree(LocalDate date, List<Shift> shifts) {

        if(shifts.isEmpty()) return true;

        // List of all the shifts that are considered main work days
        List<LocalDate> workDays = shifts.stream().map(this::identifyMainDay).toList();

        for (int i = 0; i < 5; i++) {
            LocalDate startOfPeriod = date.minusDays(i);
            LocalDate endOfPeriod = startOfPeriod.plusDays(4);

            if (isPeriodWorkFree(startOfPeriod, endOfPeriod, workDays)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPeriodWorkFree(LocalDate start, LocalDate end, List<LocalDate> workDays) {

        LocalDate current = start;

        while(!current.isAfter(end)) {
            if(workDays.contains(current)) {
                return false;
            }
            current = current.plusDays(1);
        }
        return true;
    }

    public String validateParentalLeaveDay(ParentalLeave parentalLeave,
                                           List<Shift> weeklyShifts,
                                           double fridayExtent,
                                           double mondayExtent) {

        if (!isWeekend(parentalLeave.getDate())) {
            return null;
        }

        if (is5DayFree(parentalLeave.getDate(), weeklyShifts)) {
            return null;
        }

        boolean valid = isWeekendClaimValid(parentalLeave.getDate(), fridayExtent, mondayExtent, parentalLeave.getExtent());

        if (!valid) {
            return "Warning: Claim on " + parentalLeave.getDate() +
                    " requires at least " + parentalLeave.getExtent() + " extent on connecting weekday.";
        }

        return null;
    }
}
