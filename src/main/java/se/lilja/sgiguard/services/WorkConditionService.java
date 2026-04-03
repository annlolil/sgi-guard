package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.WorkConditionRepository;

@Service
public class WorkConditionService implements WorkConditionInterface {

    private final WorkConditionRepository workConditionRepository;
    private final PersonRepository personRepository;

    @Autowired
    public WorkConditionService(WorkConditionRepository workConditionRepository, PersonRepository personRepository) {
        this.workConditionRepository = workConditionRepository;
        this.personRepository = personRepository;
    }

    @Override
    public WorkCondition addWorkCondition(WorkCondition workCondition, Long personId) {
        // Get the person that is logged in and connect it to the workconditions that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        workCondition.setPerson(person);
        workConditionRepository.save(workCondition);
        return workCondition;
    }
}
