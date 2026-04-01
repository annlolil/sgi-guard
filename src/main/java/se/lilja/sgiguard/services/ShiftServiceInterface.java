package se.lilja.sgiguard.services;

import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;

import java.security.Principal;
import java.util.List;

public interface ShiftServiceInterface {

    Shift addShift(Shift shift, Long personId);
    Shift updateShift();
    Shift getShift();
    List<Shift> getShifts(Person person);
    List<Shift> getAllShifts();
    void deleteShift();
}
