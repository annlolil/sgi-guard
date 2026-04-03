package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.PersonRepository;

@Service
public class PersonService implements PersonServiceInterface {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person addPerson(Person person) {
        personRepository.save(person);
        return person;
    }

    @Override
    public Person updatePerson() {
        return null;
    }

    @Override
    public Person getPerson() {
        return null;
    }

    @Override
    public void deletePerson() {

    }
}
