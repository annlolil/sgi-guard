package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.repositories.WorkConditionRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class SgiCalculationService {

    private final ShiftRepository shiftRepository;
    private final WorkConditionRepository workConditionRepository;
    private final PersonRepository personRepository;

    @Autowired
    public SgiCalculationService(ShiftRepository shiftRepository,
                                 WorkConditionRepository workConditionRepository,
                                 PersonRepository personRepository)
    {
        this.shiftRepository = shiftRepository;
        this.workConditionRepository = workConditionRepository;
        this.personRepository = personRepository;
    }

    // A method that takes a nightshift and identifies which day is the main day of working hours
    private LocalDate getMainDayOfWorkingHours(Shift shift) {
        
        LocalDate mainDay = null;

        // Get the specific shifts start and end time and date
        LocalDate startDate = shift.getShiftStartDate();
        LocalDate endDate = shift.getShiftEndDate();
        LocalTime startTime = shift.getShiftStartTime();
        LocalTime endTime = shift.getShiftEndTime();

        LocalDateTime start = getLocalDateTime(startDate, startTime);

        LocalDateTime midnight = start.toLocalDate().plusDays(1).atStartOfDay();

        Duration duration = Duration.between(start, midnight);
        long hours = duration.toHours();
        long minutes = duration.toMinutes();


        return mainDay;
    }
}
