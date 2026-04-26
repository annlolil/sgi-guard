package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
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
    private final Employment emp1 = new Employment();


//    @Test
//    void calculateTotalWeeklyTarget_ShouldReturnTotalWeeklyTarget() {
//        // Given
//        LocalDate validFrom = LocalDate.of(2023, 1, 1);
//        LocalDate validTo = LocalDate.of(2023, 12, 31);
//        Employment employment1 = new Employment();
//        employment1.setId(1L);
//        employment1.setOriginalEmploymentRate(100.0);
//        employment1.setCurrentEmploymentRate(85.0);
//        employment1.setOriginalWorkingHours(34.2);
//        employment1.setValidFrom(validFrom);
//        Employment employment2 = new Employment();
//        employment2.setId(2L);
//        employment2.setOriginalEmploymentRate(50.0);
//        employment2.setCurrentEmploymentRate(10.0);
//        employment2.setOriginalWorkingHours(20.0);
//        employment2.setValidFrom(validFrom);
//        employment2.setValidTo(validTo);
//        Person person = new Person();
//        person.setId(1L);
//        employment1.setPerson(person);
//        employment2.setPerson(person);
//        LocalDate from = LocalDate.of(2024, 1, 1);
//        LocalDate to = LocalDate.of(2024, 1, 31);
//
//        when(employmentRepository.findByPersonId(person.getId())).thenReturn(List.of(employment1, employment2));
//
//        // When
//        Double result = sgiCalculationService.calculateTotalWeeklyTarget(person.getId(), from, to);
//
//        // Then
//        assertThat(result).isEqualTo(29.07);
//    }

//    @Test
//    void summarizeWorkHoursInPeriod_ShouldIncludeFullShift_WhenMainDayIsInsidePeriod() {
//        // Given
//        Long personId = 1L;
//        LocalDate from = LocalDate.of(2024, 1, 1);
//        LocalDate to = LocalDate.of(2024, 1, 31);
//
//        // An 8-hour shift
//        shift1.setShiftStart(LocalDateTime.of(2023, 12, 31, 22, 0));
//        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 1, 6, 0));
//
//        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
//                .thenReturn(List.of(shift1));
//
//        // When
//        Double result = sgiCalculationService.summarizeWorkHoursInPeriod(personId, from, to);
//
//        // Then
//        assertThat(result).isEqualTo(8.0);
//    }
//
//    @Test
//    void summarizeWorkHoursInPeriod_ShouldExcludeShift_WhenMainDayIsOutsidePeriod() {
//        // Given
//        Long personId = 1L;
//        LocalDate from = LocalDate.of(2024, 1, 1);
//        LocalDate to = LocalDate.of(2024, 1, 31);
//
//        shift1.setShiftStart(LocalDateTime.of(2024, 1, 31, 22, 0));
//        shift1.setShiftEnd(LocalDateTime.of(2024, 2, 1, 6, 0));
//
//        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
//                .thenReturn(List.of(shift1));
//
//        // When
//        Double result = sgiCalculationService.summarizeWorkHoursInPeriod(personId, from, to);
//
//        // Then
//        assertThat(result).isEqualTo(0.0);
//    }

//    @Test
//    void calculateSgiStatus_ShouldReturnRecommendedDaysToClaim() {
//        // Given
//        Long personId = 1L;
//        // Period to calculate, One month, Januari 2024
//        LocalDate from = LocalDate.of(2024, 1, 1);
//        LocalDate to = LocalDate.of(2024, 1, 31);
//
//        // One employment that is valid inside the period
//        LocalDate validFrom = LocalDate.of(2023, 1, 1);
//        emp1.setOriginalWorkingHours(20.0);
//        emp1.setOriginalEmploymentRate(100.0);
//        emp1.setValidFrom(validFrom);
//        emp1.setValidTo(null);
//
//        when(employmentRepository.findByPersonId(personId)).thenReturn(List.of(emp1));
//
//        // A shift that starts and ends within the period, 1 januari 2024 20.00 to 2 januari 06.00
//        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
//        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
//
//        when(shiftRepository.findOverlappingShifts(eq(personId), any(), any()))
//                .thenReturn(List.of(shift1));
//
//        // When
//        SgiWeeklyAnalysisResponse response = sgiCalculationService.analyzeWeek(personId, from, to);
//
//        // Then
//        assertThat(response.getStatus()).isEqualTo(AT_RISK);
//        assertThat(response.getTargetHours()).isEqualTo(88.57);
//        assertThat(response.getPlannedWorkHours()).isEqualTo(10.0);
//        assertThat(response.getGapHours()).isEqualTo(78.57);
//        assertThat(response.getRecommendedDays()).isEqualTo(19.75);
//    }

//    @Test
//    void calculateSgiStatus_ShouldReturnIllegalArgumentException_WhenToIsBeforeFrom() {
//        // Given
//        long personId = 1L;
//        LocalDate from = LocalDate.of(2024, 2, 1);
//        LocalDate to = LocalDate.of(2024, 1, 31);
//
//        // When & Then
//        assertThrows(IllegalArgumentException.class, () ->
//                sgiCalculationService.calculateSgiStatus(personId, from, to)
//        );
//    }
//
//    @Test
//    void calculateSgiStatus_ShouldReturnIllegalArgumentException_WhenFromIsNull() {
//        // Given
//        Long personId = 1L;
//        LocalDate from = null;
//        LocalDate to = LocalDate.of(2024, 1, 31);
//
//        // When & Then
//        assertThrows(IllegalArgumentException.class, () ->
//                sgiCalculationService.calculateSgiStatus(personId, from, to)
//        );
//    }

    @Test
    void calculateRecommendedDaysToClaim_ShouldReturnRecommendedDaysToClaim() {
        // Given
        long personId = 1L;
        double gapHours = 5;

        Employment employment = new Employment();
        employment.setOriginalWorkingHours(34.2);

        // 34,2 / 5 = 6,84 h/day
        // 5 / 6,84 = 0,73 will be rounded up to 0,75 parental leave days

        when(employmentRepository.findByPersonId(personId)).thenReturn(List.of(employment));

        // When
        double result = sgiCalculationService.calculateRecommendedDaysToClaim(personId, gapHours);

        // Then
        assertThat(result).isEqualTo(0.75);
    }

    @Test
    void calculateRecommendedDaysToClaim_ShouldReturnZero_WhenGapHoursAreZero() {
        // Given
        long personId = 1L;
        double gapHours = 0;

        // When
        double result = sgiCalculationService.calculateRecommendedDaysToClaim(personId, gapHours);

        // Then
        assertThat(result).isEqualTo(0);
    }
}