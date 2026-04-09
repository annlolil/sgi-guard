package se.lilja.sgiguard.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.WorkCondition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShiftRepositoryTest{

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Person testPerson;
    private WorkCondition testWorkCondition;

    @BeforeEach
    void setUp(){
        testPerson = new Person();
        testPerson.setPersonalNumber("2000-01-01-1212");
        testPerson.setFirstName("John");
        testPerson.setLastName("Smith");
        testPerson = testEntityManager.persistFlushFind(testPerson);

        testWorkCondition = new WorkCondition();
        testWorkCondition.setWorkPlaceName("Hospital");
        testWorkCondition.setValidFrom(LocalDate.of(2024,1,1));
        testWorkCondition.setOriginalWorkingHours(34.2);
        testWorkCondition.setOriginalEmploymentRate(100.0);
        testWorkCondition.setCurrentEmploymentRate(100.0);
        testWorkCondition.setPerson(testPerson);
        testWorkCondition = testEntityManager.persistFlushFind(testWorkCondition);
    }

    // Test for shifts going over an end bound of a period.
    // Not taking notice of the fact that the workconditions valid to is not set
    @Test
    void findOverlappingShifts_ShouldFindShift_WhenItSpansOverEndBound() {
        // Given
        Shift shift = new Shift();
        shift.setPerson(testPerson);
        shift.setWorkCondition(testWorkCondition);
        shift.setShiftStart(LocalDateTime.of(2024,2, 29, 21, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 3, 1, 7, 0));

        shiftRepository.save(shift);

        // When:
        LocalDateTime rangeStart = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(2024, 2, 29, 23, 59, 59);

        List<Shift> result = shiftRepository.findOverlappingShifts(testPerson.getId(), rangeStart, rangeEnd);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getShiftStart().getDayOfMonth()).isEqualTo(29);
    }

    @Test
    void findOverlappingShifts_ShouldFindShift_WhenItStartsBeforePeriodButEndsInside() {
        // Given
        Shift shift = new Shift();
        shift.setPerson(testPerson);
        shift.setWorkCondition(testWorkCondition);
        shift.setShiftStart(LocalDateTime.of(2024,2, 29, 21, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 3, 1, 7, 0));

        shiftRepository.save(shift);

        // When
        LocalDateTime rangeStart = LocalDateTime.of(2024, 3, 1, 0, 0, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(2024, 4, 30, 23, 59, 59);

        List<Shift> result = shiftRepository.findOverlappingShifts(testPerson.getId(), rangeStart, rangeEnd);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getShiftEnd().getMonthValue()).isEqualTo(3);
        assertThat(result.getFirst().getShiftEnd().getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void findOverlappingShifts_ShouldReturnZero_WhenItStartsBeforePeriodAndEndsBeforePeriod() {
        // Given
        Shift shift = new Shift();
        shift.setPerson(testPerson);
        shift.setWorkCondition(testWorkCondition);
        shift.setShiftStart(LocalDateTime.of(2024,2, 29, 21, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 3, 1, 7, 0));

        shiftRepository.save(shift);

        // When
        LocalDateTime rangeStart = LocalDateTime.of(2024, 3, 1, 8, 0, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(2024, 4, 30, 23, 59, 59);

        List<Shift> result = shiftRepository.findOverlappingShifts(testPerson.getId(), rangeStart, rangeEnd);

        // Then
        assertThat(result).hasSize(0);
    }

    @Test
    void findOverlappingShifts_ShouldReturnZero_WhenItStartsAfterPeriod() {
        // Given
        Shift shift = new Shift();
        shift.setPerson(testPerson);
        shift.setWorkCondition(testWorkCondition);
        shift.setShiftStart(LocalDateTime.of(2024,3, 1, 0, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 3, 1, 8, 0));

        shiftRepository.save(shift);

        // When
        LocalDateTime rangeStart = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(2024, 2, 29, 23, 59, 59);

        List<Shift> result = shiftRepository.findOverlappingShifts(testPerson.getId(), rangeStart, rangeEnd);

        // Then
        assertThat(result).hasSize(0);
    }

    @Test
    void findOverlappingShifts_ShouldReturnOne_WhenItStartsAndEndsInAPeriod() {
        // Given
        Shift shift = new Shift();
        shift.setPerson(testPerson);
        shift.setWorkCondition(testWorkCondition);
        shift.setShiftStart(LocalDateTime.of(2024,2, 1, 8, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 2, 1, 16, 0));

        shiftRepository.save(shift);

        // When
        LocalDateTime rangeStart = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(2024, 2, 29, 23, 59, 59);

        List<Shift> result = shiftRepository.findOverlappingShifts(testPerson.getId(), rangeStart, rangeEnd);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void findShiftByPersonId_ShouldReturnOnlyShiftsBelongingToSpecificPerson() {
        // Given
        Person anotherPerson = new Person();
        anotherPerson.setPersonalNumber("1990-05-05-5555");
        anotherPerson.setFirstName("Jane");
        anotherPerson.setLastName("Doe");
        anotherPerson = testEntityManager.persistFlushFind(anotherPerson);

        Shift shift1 = new Shift();
        shift1.setPerson(testPerson);
        shift1.setWorkCondition(testWorkCondition);
        shift1.setShiftStart(LocalDateTime.now());
        shift1.setShiftEnd(LocalDateTime.now().plusHours(8));
        shiftRepository.save(shift1);

        Shift shift2 = new Shift();
        shift2.setPerson(anotherPerson);
        shift2.setWorkCondition(testWorkCondition);
        shift2.setShiftStart(LocalDateTime.now());
        shift2.setShiftEnd(LocalDateTime.now().plusHours(8));
        shiftRepository.save(shift2);

        // When
        List<Shift> result = shiftRepository.findShiftByPersonId(testPerson.getId());

        // THen
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPerson().getId()).isEqualTo(testPerson.getId());
    }
}