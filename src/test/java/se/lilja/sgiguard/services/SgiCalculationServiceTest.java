package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.dtos.SgiDailyAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
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
import static se.lilja.sgiguard.models.SgiStatus.PROTECTED;

@ExtendWith(MockitoExtension.class)
class SgiCalculationServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private EmploymentRepository employmentRepository;

    @Mock
    private ParentalLeaveRepository parentalLeaveRepository;

    @InjectMocks
    private SgiCalculationService sgiCalculationService;

    private final Shift shift1 = new Shift();
    private final Employment emp1 = new Employment();

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

//    @Test
//    void calculateRecommendedDaysToClaim_ShouldReturnRecommendedDaysToClaim() {
//        // Given
//        long personId = 1L;
//        double gapHours = 5;
//
//        Employment employment = new Employment();
//        employment.setOriginalWorkingHours(34.2);
//
//        // 34,2 / 5 = 6,84 h/day
//        // 5 / 6,84 = 0,73 will be rounded up to 0,75 parental leave days
//
//        when(employmentRepository.findByPersonId(personId)).thenReturn(List.of(employment));
//
//        // When
//        double result = sgiCalculationService.calculateRecommendedDaysToClaim(personId, gapHours);
//
//        // Then
//        assertThat(result).isEqualTo(0.75);
//    }
//
//    @Test
//    void calculateRecommendedDaysToClaim_ShouldReturnZero_WhenGapHoursAreZero() {
//        // Given
//        long personId = 1L;
//        double gapHours = 0;
//
//        // When
//        double result = sgiCalculationService.calculateRecommendedDaysToClaim(personId, gapHours);
//
//        // Then
//        assertThat(result).isEqualTo(0);
//    }

    @Test
    void analyzeDay_ShouldReturnProtected_WhenParentalLeaveCoversGap() {

        // Given
        Long personId = 1L;
        LocalDate date = LocalDate.of(2024, 4, 1);

        Shift baselineShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,8,0))
                .shiftEnd(LocalDateTime.of(2024,4,1,17,0))
                .type(ShiftType.BASELINE)
                .build();

        Shift actualShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,8,0))
                .shiftEnd(LocalDateTime.of(2024,4,1,15,0))
                .type(ShiftType.ACTUAL)
                .build();

        ParentalLeave leave = ParentalLeave.builder()
                .date(date)
                .extent(0.25)
                .build();

        when(shiftRepository.findOverlappingShifts(
                eq(personId),
                any(),
                any()
        )).thenReturn(List.of(baselineShift, actualShift));

        when(parentalLeaveRepository.findByPersonIdAndDate(
                personId,
                date
        )).thenReturn(List.of(leave));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personId, date);

        // Then
        assertThat(response.getStatus())
                .isEqualTo(SgiStatus.PROTECTED);

        assertThat(response.getRecommendedExtent())
                .isEqualTo(0.25);
    }

    @Test
    void analyzeDay_ShouldReturnAtRisk_WhenParentalLeaveIsMissing() {

        // Given
        Long personId = 1L;
        LocalDate date = LocalDate.of(2024, 4, 1);

        Shift baselineShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,8,0))
                .shiftEnd(LocalDateTime.of(2024,4,1,17,0))
                .type(ShiftType.BASELINE)
                .build();

        Shift actualShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,8,0))
                .shiftEnd(LocalDateTime.of(2024,4,1,15,0))
                .type(ShiftType.ACTUAL)
                .build();

        when(shiftRepository.findOverlappingShifts(
                eq(personId),
                any(),
                any()
        )).thenReturn(List.of(baselineShift, actualShift));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personId, date);

        // Then
        assertThat(response.getStatus())
                .isEqualTo(SgiStatus.AT_RISK);

        assertThat(response.getRecommendedExtent())
                .isEqualTo(0.25);
    }

    @Test
    void analyzeDay_ShouldReturnOvercompensated_WhenParentalLeaveExceedsGap() {
        // Given
        Long personId = 1L;
        LocalDate date = LocalDate.of(2024, 4, 1);

        Shift baselineShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,8,0))
                .shiftEnd(LocalDateTime.of(2024,4,1,17,0))
                .type(ShiftType.BASELINE)
                .build();

        Shift actualShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,8,0))
                .shiftEnd(LocalDateTime.of(2024,4,1,15,0))
                .type(ShiftType.ACTUAL)
                .build();

        ParentalLeave leave = ParentalLeave.builder()
                .date(date)
                .extent(1.0)
                .build();

        when(shiftRepository.findOverlappingShifts(
                eq(personId),
                any(),
                any()
        )).thenReturn(List.of(baselineShift, actualShift));

        when(parentalLeaveRepository.findByPersonIdAndDate(
                personId,
                date
        )).thenReturn(List.of(leave));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personId, date);

        // Then
        assertThat(response.getStatus())
                .isEqualTo(SgiStatus.OVERCOMPENSATED);

        assertThat(response.getRecommendedExtent())
                .isEqualTo(0.25);
    }

    @Test
    void analyzeDay_ShouldSplitNightShiftHoursBetweenDays() {
        // Given
        Long personId = 1L;
        LocalDate date = LocalDate.of(2024, 4, 1);

        Shift baselineShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,21,0))
                .shiftEnd(LocalDateTime.of(2024,4,2,7,0))
                .type(ShiftType.BASELINE)
                .build();

        Shift actualShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,21,0))
                .shiftEnd(LocalDateTime.of(2024,4,2,7,0))
                .type(ShiftType.ACTUAL)
                .build();

        when(shiftRepository.findOverlappingShifts(
                eq(personId),
                any(),
                any()
        )).thenReturn(List.of(baselineShift, actualShift));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personId, date);

        // Then
        assertThat(response.getActualHours()).isEqualTo(3.0);
    }

    @Test
    void analyzeDay_ShouldReturnZero_WhenNoBaselineShiftExists() {
        // Given
        Long personId = 1L;
        LocalDate date = LocalDate.of(2024, 4, 1);

        Shift actualShift = Shift.builder()
                .shiftStart(LocalDateTime.of(2024,4,1,21,0))
                .shiftEnd(LocalDateTime.of(2024,4,2,7,0))
                .type(ShiftType.ACTUAL)
                .build();

        when(shiftRepository.findOverlappingShifts(
                eq(personId),
                any(),
                any()
        )).thenReturn(List.of(actualShift));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personId, date);

        // Then
        assertThat(response.getBaselineHours()).isEqualTo(0.0);
    }
}