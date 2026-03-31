package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.security.Principal;
import java.util.List;

@Service
public class ShiftService implements ShiftServiceInterface {

    private final ShiftRepository shiftRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public Shift addShift(Shift shift) {
        shiftRepository.save(shift);
        return shift;
    }

    @Override
    public Shift updateShift() {
        return null;
    }

    @Override
    public Shift getShift() {
        return null;
    }

    @Override
    public List<Shift> getShifts(Person person) {
        return shiftRepository.findShiftByPerson(person);
    }

    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public void deleteShift() {

    }
}
