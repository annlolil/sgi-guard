package se.lilja.sgiguard.services;

import se.lilja.sgiguard.dtos.ShiftDTO;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import java.util.List;

public interface ShiftServiceInterface {

    Shift addShift(ShiftDTO shiftDTO, Long personId, Long workConditionId);
    Shift updateShift();
    Shift getShift();
    List<Shift> getShifts(Long personId);
    List<Shift> getAllShifts();
    void deleteShift();
}
