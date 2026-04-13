package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SgiCalculationServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private EmploymentRepository employmentRepository;

    @InjectMocks
    private SgiCalculationService sgiCalculationService;

    private final Shift shift = new Shift();


    @Test
    void identifyMainDay_ShouldReturnSecondDay_WhenMoreHoursOnSecondDay() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay.plusDays(1);

        // 20:00 to 06:00 (4h on day 1, 6h on day 2)
        shift.setShiftStart(startDay.atTime(20, 0));
        shift.setShiftEnd(nextDay.atTime(6, 0));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift);

        // Then
        assertThat(result).isEqualTo(nextDay);
    }

    @Test
    void identifyMainDay_ShouldReturnFirstDay_WhenHoursAreTheSameOnFirstDayAndSecondDay() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay.plusDays(1);

        // 20:00 to 04:00 (4h on day 1, 4h on day 2)
        shift.setShiftStart(startDay.atTime(20, 0));
        shift.setShiftEnd(nextDay.atTime(4, 0));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift);

        // Then
        assertThat(result).isEqualTo(startDay);
    }

    @Test
    void identifyMainDay_ShouldReturnFirstDay_WhenFirstAndSecondDayAreTheSame() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay;

        // 08:00 to 17:00 on the same day
        shift.setShiftStart(startDay.atTime(8, 0));
        shift.setShiftEnd(nextDay.atTime(17, 0));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift);

        assertThat(result).isEqualTo(startDay);
    }

    @Test
    void calculateTotalWeeklyTarget_ShouldReturnTotalWeeklyTarget() {
        // Given
        LocalDate validFrom = LocalDate.of(2024, 1, 1);
        Employment employment1 = new Employment();
        employment1.setId(1L);
        employment1.setOriginalEmploymentRate(100.0);
        employment1.setCurrentEmploymentRate(85.0);
        employment1.setOriginalWorkingHours(34.2);
        employment1.setValidFrom(validFrom);
        Employment employment2 = new Employment();
        employment2.setId(2L);
        employment2.setOriginalEmploymentRate(50.0);
        employment2.setCurrentEmploymentRate(10.0);
        employment2.setOriginalWorkingHours(20.0);
        employment2.setValidFrom(validFrom);
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setPersonalNumber("2000-01-01-1212");
        person.setId(1L);
        employment1.setPerson(person);
        employment2.setPerson(person);

        // When
        when(employmentRepository.findByPersonId(person.getId())).thenReturn(List.of(employment1, employment2));
        Double result = sgiCalculationService.calculateTotalWeeklyTarget(person.getId());

        // Then
        assertThat(result).isEqualTo(31.07);
    }

    @Test
    void summarizeWorkHoursInPeriod_ShouldIncludeFullShift_WhenMainDayIsInsidePeriod() {
        // Given
        Long personId = 1L;
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        // An 8-hour shift
        Shift shift = new Shift();
        shift.setShiftStart(LocalDateTime.of(2023, 12, 31, 22, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 1, 1, 6, 0));

        // When
        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
                .thenReturn(List.of(shift));

        Double result = sgiCalculationService.summarizeWorkHoursInPeriod(personId, from, to);

        // Then
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    void summarizeWorkHoursInPeriod_ShouldExcludeShift_WhenMainDayIsOutsidePeriod() {
        // Given
        Long personId = 1L;
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        Shift shift = new Shift();
        shift.setShiftStart(LocalDateTime.of(2024, 1, 31, 22, 0));
        shift.setShiftEnd(LocalDateTime.of(2024, 2, 1, 6, 0));

        // When
        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
                .thenReturn(List.of(shift));

        Double result = sgiCalculationService.summarizeWorkHoursInPeriod(personId, from, to);

        // Then
        assertThat(result).isEqualTo(0.0);
    }
}