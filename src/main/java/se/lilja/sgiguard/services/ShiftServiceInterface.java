package se.lilja.sgiguard.services;

import org.springframework.cglib.core.Local;
import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.dtos.ShiftResponse;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;

import java.time.LocalDate;
import java.util.List;

public interface ShiftServiceInterface {

    ShiftResponse addShift(Person person, ShiftRequest shiftRequest);

    void deleteShiftsInWeek(Long personId, LocalDate date);
}
