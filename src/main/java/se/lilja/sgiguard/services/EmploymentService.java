package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.EmploymentRepository;

@Service
public class EmploymentService implements EmploymentInterface {

    private final EmploymentRepository employmentRepository;
    private final PersonRepository personRepository;

    @Autowired
    public EmploymentService(EmploymentRepository employmentRepository, PersonRepository personRepository) {
        this.employmentRepository = employmentRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Employment addEmployment(Employment employment, Long personId) {
        // Get the person that is logged in and connect it to the employment that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        if (employment.getValidTo() != null) {
            if (employment.getValidTo().isBefore(employment.getValidFrom())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date");
            }
        }

        employment.setPerson(person);
        employmentRepository.save(employment);
        return employment;
    }
}
