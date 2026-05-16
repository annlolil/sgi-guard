package se.lilja.sgiguard.services;

import se.lilja.sgiguard.dtos.PersonRequest;
import se.lilja.sgiguard.dtos.PersonResponse;
import se.lilja.sgiguard.entities.Person;

public interface PersonServiceInterface {

    PersonResponse addPerson(PersonRequest request);

    Person getByPersonalNumber(String personalNumber);
}
