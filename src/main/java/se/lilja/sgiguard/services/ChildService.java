package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.entities.Child;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.ChildRepository;
import se.lilja.sgiguard.repositories.PersonRepository;

@Service
public class ChildService implements ChildServiceInterface {

    private final ChildRepository childRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ChildService(ChildRepository childRepository, PersonRepository personRepository) {
        this.childRepository = childRepository;
        this.personRepository = personRepository;
    }

    public Child addChild(Child child, Long personId) {
        // Get the person that is logged in and connect it to the child that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        child.setPerson(person);
        childRepository.save(child);

        return child;
    }

    @Override
    public Child updateChild(Child child) {
        return null;
    }

    @Override
    public void deleteChild(Child child) {

    }
}
