package se.lilja.sgiguard.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.PersonRequest;
import se.lilja.sgiguard.dtos.PersonResponse;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.PersonRepository;

import java.util.Optional;

@Service
public class PersonService implements PersonServiceInterface {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public PersonResponse addPerson(PersonRequest request) {

        if(personRepository.existsPersonByPersonalNumber(request.getPersonalNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Person already exists");
        }

        Person person = Person.builder()
                .personalNumber(request.getPersonalNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        Person savedPerson = personRepository.save(person);

        return new PersonResponse(savedPerson.getFirstName(), savedPerson.getLastName());
    }
}
