package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.repositories.WorkConditionRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftService implements ShiftServiceInterface {

    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;
    private final WorkConditionRepository workConditionRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository, PersonRepository personRepository, WorkConditionRepository workConditionRepository) {
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
        this.workConditionRepository = workConditionRepository;
    }

    @Override
    public Shift addShift(Shift shift, Long personId, Long workConditionId) {
        // Get the person that is logged in and connect it to the shift that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        WorkCondition workCondition = workConditionRepository.findById(workConditionId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Workcondition not found"));

        shift.setPerson(person);
        shift.setWorkCondition(workCondition);
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
    public List<Shift> getShifts(Long personId) {
        return shiftRepository.findShiftByPersonId(personId);
    }

    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public void deleteShift() {

    }
}
