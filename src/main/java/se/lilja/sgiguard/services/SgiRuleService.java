package se.lilja.sgiguard.services;

import org.springframework.stereotype.Service;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public boolean isWeekendClaimValid(double fridayExtent, double mondayExtent, double currentExtent) {

        return fridayExtent >= currentExtent || mondayExtent >= currentExtent;
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

    public String validateParentalLeave(ParentalLeave parentalLeave,
                                           List<Shift> shifts,
                                           List<ParentalLeave> existingLeaves,
                                           double fridayExtent,
                                           double mondayExtent) {

        // Extent > 0
        if(validateExtent(parentalLeave, existingLeaves)) {
            return "Total parental leave extent can not exceed 100 % on one day";
        }

        // Weekend rule
        if (isWeekend(parentalLeave.getDate()) && !is5DayFree(parentalLeave.getDate(), shifts)) {

            boolean valid = isWeekendClaimValid(
                    fridayExtent, mondayExtent, parentalLeave.getExtent());

            if(!valid) {
                return "Warning weekend rule violation";
            }

        }
        return null;
    }

    public boolean validateWeekendRule(ParentalLeave parentalLeave, List<Shift> shifts, double fridayExtent, double mondayExtent) {

        // Weekend rule
        if (isWeekend(parentalLeave.getDate()) && !is5DayFree(parentalLeave.getDate(), shifts)) {

            boolean valid = isWeekendClaimValid(
                    fridayExtent, mondayExtent, parentalLeave.getExtent());

            if(!valid) {
                return false;
            }

        }
        return true;
    }

    public boolean validateExtent(ParentalLeave newLeave, List<ParentalLeave> existingLeaves) {

        double existingExtent = existingLeaves.stream()
                .filter(leave -> leave.getDate().equals(newLeave.getDate()))
                .mapToDouble(ParentalLeave::getExtent)
                .sum();

        double totalExtent = existingExtent + newLeave.getExtent();

        return !(totalExtent > 1.0);
    }

    public List<String> getParentalLeaveWarnings(
            ParentalLeave parentalLeave,
            List<Shift> shifts,
            List<ParentalLeave> sameDayLeaves,
            double fridayExtent,
            double mondayExtent) {

        List<String> warnings = new ArrayList<>();

        if(!validateExtent(parentalLeave, sameDayLeaves)) {
            warnings.add("Parental leave exceeds 100% on one day");
        }

        if(!validateWeekendRule(parentalLeave, shifts, fridayExtent, mondayExtent)) {

                warnings.add("Weekend rule may not be fulfilled");
            }
        return warnings;
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
//        if(validateExtent(parentalLeave, sameDayLeaves)) {
//            warnings.add("Parental leave exceeds 100% on one day");
//        }
//
//        if(isWeekend(parentalLeave.getDate())
//                && !is5DayFree(parentalLeave.getDate(), shifts)) {
//
//            boolean valid = isWeekendClaimValid(
//                    fridayExtent,
//                    mondayExtent,
//                    parentalLeave.getExtent()
//            );
//
//            if(!valid) {
//                warnings.add("Weekend rule may not be fulfilled");
//            }
//        }
//
//        return warnings;
//    }
}
