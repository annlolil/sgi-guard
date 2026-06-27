package se.lilja.sgiguard.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShiftRepositoryTest{

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Person person;

    @BeforeEach
    void setUp() {
        person = testEntityManager.persistFlushFind(Person.builder()
                .personalNumber("2000-01-01-1212").firstName("John").lastName("Smith").build());
    }

    // Create a shift
    private void createShift(LocalDateTime start, LocalDateTime end, Person person) {
        testEntityManager.persist(Shift.builder()
                .shiftStart(start).shiftEnd(end).person(person).build());
    }

    @Test
    void findOverlappingShifts_ShouldFindShift_WhenItStartsInsidePeriodButEndsAfter() {

        // Given
        createShift(
                LocalDateTime.of(2024, 2, 29, 21, 0),
                LocalDateTime.of(2024, 3, 1, 7, 0), person);
        testEntityManager.flush();

        // When
        var result = shiftRepository.findOverlappingShifts(person.getId(),
                LocalDateTime.of(2024, 2, 1, 0, 0),
                LocalDateTime.of(2024, 2, 29, 23, 59));

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingShifts_ShouldFindShift_WhenItStartsBeforePeriodButEndsInside() {

        // Given
        createShift(
                LocalDateTime.of(2024, 1, 29, 23, 59),
                LocalDateTime.of(2024, 2, 1, 7, 0), person);
        testEntityManager.flush();

        // When
        var result = shiftRepository.findOverlappingShifts(person.getId(),
                LocalDateTime.of(2024, 2, 1, 0, 0),
                LocalDateTime.of(2024, 2, 29, 23, 59));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getShiftEnd().getMonthValue()).isEqualTo(2);
        assertThat(result.getFirst().getShiftEnd().getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void findOverlappingShifts_ShouldFindShift_WhenItStartsAndEndsInAPeriod() {

        // Given
        createShift(
                LocalDateTime.of(2024, 1, 29, 0, 0),
                LocalDateTime.of(2024, 2, 1, 7, 0), person);
        testEntityManager.flush();

        // When
        var result = shiftRepository.findOverlappingShifts(person.getId(),
                LocalDateTime.of(2024, 1, 29, 0, 0),
                LocalDateTime.of(2024, 2, 29, 23, 59));

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingShifts_ShouldReturnZero_WhenItStartsAfterPeriod() {

        // Given
        createShift(
                LocalDateTime.of(2024,3, 1, 0, 0),
                LocalDateTime.of(2024, 3, 1, 8, 0), person);
        testEntityManager.flush();

        // When
        var result = shiftRepository.findOverlappingShifts(person.getId(),
                LocalDateTime.of(2024, 2, 1, 0, 0),
                LocalDateTime.of(2024, 2, 29, 23, 59));

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findOverlappingShifts_ShouldReturnZero_WhenItEndsBeforePeriod() {

        // Given
        createShift(
                LocalDateTime.of(2024,1, 29, 13, 0),
                LocalDateTime.of(2024, 1, 29, 23, 59), person);
        testEntityManager.flush();

        // When
        var result = shiftRepository.findOverlappingShifts(person.getId(),
                LocalDateTime.of(2024, 2, 1, 0, 0),
                LocalDateTime.of(2024, 2, 1, 23, 59));

        // Then
        assertThat(result).hasSize(0);
    }

    @Test
    void findShiftByPersonId_ShouldReturnOnlyShiftsBelongingToSpecificPerson() {

        // Given
        Person another = testEntityManager.persistFlushFind(Person.builder()
                .personalNumber("1990-05-05-5555").firstName("Jane").lastName("Doe").build());

        createShift(LocalDateTime.now(), LocalDateTime.now().plusHours(8), person);
        createShift(LocalDateTime.now(), LocalDateTime.now().plusHours(8), another);
        testEntityManager.flush();

        // When
        List<Shift> result = shiftRepository.findShiftsByPersonId(person.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPerson().getId()).isEqualTo(person.getId());
    }
}