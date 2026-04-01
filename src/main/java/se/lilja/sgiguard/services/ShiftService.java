package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftService implements ShiftServiceInterface {

    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository, PersonRepository personRepository) {
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Shift addShift(Shift shift, Long personId) {
        // Get the person that is logged in and connect it to the shift that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        shift.setPerson(person);
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
