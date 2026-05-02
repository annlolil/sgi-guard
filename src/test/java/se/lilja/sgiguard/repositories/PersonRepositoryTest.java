package se.lilja.sgiguard.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import se.lilja.sgiguard.entities.Person;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setPersonalNumber("200001011212");
        testEntityManager.persistFlushFind(person);
    }

    @Test
    void existsPersonByPersonalNumber_ShouldReturnTrue_WhenPersonExists() {

        // Given
        String personalNumber = person.getPersonalNumber();

        // When
        boolean exists = personRepository.existsPersonByPersonalNumber(personalNumber);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsPersonByPersonalNumber_ShouldReturnFalse_WhenPersonNotExists() {

        // Given
        String nonExistingPersonalNumber = "190001011213";

        // When
        boolean exists = personRepository.existsPersonByPersonalNumber(nonExistingPersonalNumber);

        // Then
        assertFalse(exists);
    }
}