package se.lilja.sgiguard.services;

import se.lilja.sgiguard.entities.Person;

public interface PersonServiceInterface {

    Person addPerson(Person person);
    Person updatePerson();
    Person getPerson();
    void deletePerson(); // IS THIS NECESSARY?
}
