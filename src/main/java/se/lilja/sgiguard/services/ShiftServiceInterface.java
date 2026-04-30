package se.lilja.sgiguard.services;

import se.lilja.sgiguard.dtos.ShiftDTO;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;

import java.time.LocalDate;
import java.util.List;

public interface ShiftServiceInterface {

    Shift addShift(ShiftDTO shiftDTO, Long personId);
    List<Shift> getShifts(Long personId);
    String deleteShift(Long shiftId);
}
