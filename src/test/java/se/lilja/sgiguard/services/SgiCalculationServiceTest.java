package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.dtos.SgiStatusResponse;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static se.lilja.sgiguard.models.SgiStatus.AT_RISK;

@ExtendWith(MockitoExtension.class)
class SgiCalculationServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private EmploymentRepository employmentRepository;

    @InjectMocks
    private SgiCalculationService sgiCalculationService;

    private final Shift shift1 = new Shift();
    private final Shift shift2 = new Shift();


    @Test
    void identifyMainDay_ShouldReturnSecondDay_WhenMoreHoursOnSecondDay() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay.plusDays(1);

        // 20:00 to 06:00 (4h on day 1, 6h on day 2)
        shift1.setShiftStart(startDay.atTime(20, 0));
        shift1.setShiftEnd(nextDay.atTime(6, 0));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift1);

        // Then
        assertThat(result).isEqualTo(nextDay);
    }

    @Test
    void identifyMainDay_ShouldReturnFirstDay_WhenHoursAreTheSameOnFirstDayAndSecondDay() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay.plusDays(1);

        // 20:00 to 04:00 (4h on day 1, 4h on day 2)
        shift1.setShiftStart(startDay.atTime(20, 0));
        shift1.setShiftEnd(nextDay.atTime(4, 0));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift1);

        // Then
        assertThat(result).isEqualTo(startDay);
    }

    @Test
    void identifyMainDay_ShouldReturnFirstDay_WhenFirstAndSecondDayAreTheSame() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay;

        // 08:00 to 17:00 on the same day
        shift1.setShiftStart(startDay.atTime(8, 0));
        shift1.setShiftEnd(nextDay.atTime(17, 0));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift1);

        assertThat(result).isEqualTo(startDay);
    }

    @Test
    void calculateTotalWeeklyTarget_ShouldReturnTotalWeeklyTarget() {
        // Given
        LocalDate validFrom = LocalDate.of(2023, 1, 1);
        LocalDate validTo = LocalDate.of(2023, 12, 31);
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
        employment2.setValidTo(validTo);
        Person person = new Person();
        person.setId(1L);
        employment1.setPerson(person);
        employment2.setPerson(person);
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        when(employmentRepository.findByPersonId(person.getId())).thenReturn(List.of(employment1, employment2));

        // When
        Double result = sgiCalculationService.calculateTotalWeeklyTarget(person.getId(), from, to);

        // Then
        assertThat(result).isEqualTo(29.07);
    }

    @Test
    void summarizePlannedHoursInPeriod_ShouldIncludeFullShift_WhenMainDayIsInsidePeriod() {
        // Given
        Long personId = 1L;
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        // An 8-hour shift
        shift1.setShiftStart(LocalDateTime.of(2023, 12, 31, 22, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 1, 6, 0));

        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
                .thenReturn(List.of(shift1));

        // When
        Double result = sgiCalculationService.summarizePlannedHoursInPeriod(personId, from, to);

        // Then
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    void summarizePlannedHoursInPeriod_ShouldExcludeShift_WhenMainDayIsOutsidePeriod() {
        // Given
        Long personId = 1L;
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        shift1.setShiftStart(LocalDateTime.of(2024, 1, 31, 22, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 2, 1, 6, 0));

        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
                .thenReturn(List.of(shift1));

        // When
        Double result = sgiCalculationService.summarizePlannedHoursInPeriod(personId, from, to);

        // Then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void calculateSgiStatus_ShouldReturnIsProtected_WhenPlannedHoursAreMoreThanTargetHours() {
        // Given
        LocalDate validFrom = LocalDate.of(2023, 1, 1);
        LocalDate validTo = LocalDate.of(2023, 12, 31);

        Long personId = 1L;
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        // One employment that ends before the period and one that is active until further notice
        Employment emp1 = new Employment();
        emp1.setOriginalWorkingHours(20.0);
        emp1.setCurrentEmploymentRate(50.0);
        emp1.setValidFrom(validFrom);
        emp1.setValidTo(null);

        Employment emp2 = new Employment();
        emp2.setOriginalWorkingHours(20.0);
        emp2.setCurrentEmploymentRate(50.0);
        emp2.setValidFrom(validFrom);
        emp2.setValidTo(validTo);

        when(employmentRepository.findByPersonId(personId)).thenReturn(List.of(emp1, emp2));

        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        shift2.setShiftStart(LocalDateTime.of(2024, 1, 2, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 3, 6, 0));

        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
                .thenReturn(List.of(shift1, shift2));

        // When
        SgiStatusResponse response = sgiCalculationService.calculateSgiStatus(personId, from, to);

        // Then
        assertThat(response.getStatus()).isEqualTo(AT_RISK);
        assertThat(response.getTargetHours()).isEqualTo(44.29);
        assertThat(response.getPlannedHours()).isEqualTo(20.0);
        assertThat(response.getGapHours()).isEqualTo(24.29);
    }

    @Test
    void calculateSgiStatus_ShouldReturnIllegalArgumentException_WhenToIsBeforeFrom() {
        // Given
        Long personId = 1L;
        LocalDate from = LocalDate.of(2024, 2, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                sgiCalculationService.calculateSgiStatus(personId, from, to)
        );
    }

    @Test
    void calculateSgiStatus_ShouldReturnIllegalArgumentException_WhenFromIsNull() {
        // Given
        Long personId = 1L;
        LocalDate from = null;
        LocalDate to = LocalDate.of(2024, 1, 31);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                sgiCalculationService.calculateSgiStatus(personId, from, to)
        );
    }
}