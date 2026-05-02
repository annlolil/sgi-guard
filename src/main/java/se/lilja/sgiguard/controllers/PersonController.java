package se.lilja.sgiguard.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.lilja.sgiguard.dtos.PersonRequest;
import se.lilja.sgiguard.dtos.PersonResponse;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.services.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/addperson")
    public ResponseEntity<PersonResponse> addPerson(@Valid @RequestBody PersonRequest request) {
        return new ResponseEntity<>(personService.addPerson(request), HttpStatus.CREATED);
    }
}
