package se.lilja.sgiguard.services;

import se.lilja.sgiguard.dtos.PersonRequest;
import se.lilja.sgiguard.dtos.PersonResponse;

public interface PersonServiceInterface {

    PersonResponse addPerson(PersonRequest request);
}
