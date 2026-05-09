package se.lilja.sgiguard.services;

import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.entities.Shift;

import java.util.List;

public interface ShiftServiceInterface {

    Shift addShift(ShiftRequest shiftRequest, Long personId);
    List<Shift> getShifts(Long personId);
    String deleteShift(Long shiftId);
}
