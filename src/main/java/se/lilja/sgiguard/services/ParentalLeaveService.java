package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;

@Service
public class ParentalLeaveService {

    private final ParentalLeaveRepository parentalLeaveRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ParentalLeaveService(ParentalLeaveRepository parentalLeaveRepository, PersonRepository personRepository) {
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.personRepository = personRepository;
    }

    public ParentalLeave addParentalLeave(ParentalLeaveRequest request) {

        Person person = personRepository.findById(request.getPersonId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        ParentalLeave parentalLeave = new ParentalLeave();
        parentalLeave.setPerson(person);
        parentalLeave.setDate(request.getDate());
        parentalLeave.setExtent(request.getExtent());
        return parentalLeaveRepository.save(parentalLeave);
    }
}
