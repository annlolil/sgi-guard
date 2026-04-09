package se.lilja.sgiguard.repositories;

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

    // Test for shifts going over an end bound of a period.
    // Not taking notice of the fact that the workconditions valid to is not set
    @Test
    void findOverlappingShifts_ShouldFindShift_WhenItSpansOverEndBound() {
        // Given
        Person testPerson = new Person();
        testPerson.setPersonalNumber("2000-01-01-1212");
        testPerson.setFirstName("John");
        testPerson.setLastName("Smith");
        testPerson = testEntityManager.persistFlushFind(testPerson);

        WorkCondition testWorkCondition = new WorkCondition();
        testWorkCondition.setWorkPlaceName("Hospital");
        testWorkCondition.setValidFrom(LocalDate.of(2024,1,1));
        testWorkCondition.setOriginalWorkingHours(34.2);
        testWorkCondition.setOriginalEmploymentRate(100.0);
        testWorkCondition.setCurrentEmploymentRate(100.0);
        testWorkCondition.setPerson(testPerson);
        testWorkCondition = testEntityManager.persistFlushFind(testWorkCondition);

        Shift shift = new Shift();
        shift.setPerson(testPerson);
        shift.setShiftStart(LocalDateTime.of(2024,2, 29, 21, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 3, 1, 22, 0));
        shift.setWorkCondition(testWorkCondition);
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
    void findShiftByPersonId() {
    }
}