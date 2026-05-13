package se.lilja.sgiguard.services;

import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.dtos.ShiftResponse;
import se.lilja.sgiguard.entities.Shift;

import java.util.List;

public interface ShiftServiceInterface {

    ShiftResponse addShift(ShiftRequest shiftRequest);
//    List<Shift> getShifts(Long personId);
    String deleteShift(Long shiftId);
}
