package se.lilja.sgiguard.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SgiRuleServiceTest {

    @Mock
    ShiftRepository shiftRepository;

    @InjectMocks
    private SgiRuleService sgiRuleService;

    private Shift shift1;
    private Shift shift2;

    @BeforeEach
    void setUp() {
        shift1 = new Shift();
        shift2 = new Shift();
    }

    @Test
    void is5DayFree_shouldReturnFalse_whenPersonIsFreeLessThan5Days() {

        // Given
        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        shift1.setType(ShiftType.BASELINE);

        shift2.setShiftStart(LocalDateTime.of(2024, 1, 6, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 7, 6, 0));
        shift2.setType(ShiftType.BASELINE);

        when(shiftRepository.findOverlappingShifts(eq(1L), any(), any())).thenReturn(Arrays.asList(shift1, shift2));

        // When
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 5), 1L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenPersonIs5DayFreeAfterShift() {

        // Given
        // Shift 1 has main day 2 januari, so 2 januari is not free
        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        // Shift 2 has main day 8 januari so 3, 4, 5, 6 and 7 januari is free (5 days), but not 8 januari
        shift2.setShiftStart(LocalDateTime.of(2024, 1, 7, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 8, 6, 0));

        when(shiftRepository.findOverlappingShifts(eq(1L), any(), any())).thenReturn(Arrays.asList(shift1, shift2));

        // When
        // Looking at the first day of the free period
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 3), 1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenPersonIs5DayFreeBeforeShift() {

        // Given
        // Shift 1 has main day 2 januari, so 2 januari is not free
        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        // Shift 2 has main day 8 januari so 3, 4, 5, 6 and 7 januari is free (5 days), but not 8 januari
        shift2.setShiftStart(LocalDateTime.of(2024, 1, 7, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 8, 6, 0));

        when(shiftRepository.findOverlappingShifts(eq(1L), any(), any())).thenReturn(Arrays.asList(shift1, shift2));

        // When
        // Looking at the last day of the free period
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 7), 1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenNoShiftsAtAll() {

        // Given
        when(shiftRepository.findOverlappingShifts(eq(1L), any(), any())).thenReturn(Collections.emptyList());

        // When
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 7), 1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isExtentValid_ShouldReturnFalse_WhenTotalExtentIsExceeded(){

        // Given
        ParentalLeave newLeave = new ParentalLeave();
        newLeave.setExtent(0.75);

        double existingExtent = 0.5;

        // When
        boolean result = sgiRuleService.isExtentValid(newLeave, existingExtent);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isExtentValid_ShouldReturnTrue_WhenTotalExtentIsMaxOneDay(){

        // Given
        ParentalLeave newLeave = new ParentalLeave();
        newLeave.setExtent(0.75);

        double existingExtent = 0.25;

        // When
        boolean result = sgiRuleService.isExtentValid(newLeave, existingExtent);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isWeekendExtentValid_ShouldReturnFalse_WhenNotEnoughExtentOnSurroundingDays(){

        // Given
        double fridayExtent = 0.5;
        double mondayExtent = 0.0;
        double currentExtent = 1.0;

        // When
        boolean result = sgiRuleService.isWeekendExtentValid(fridayExtent, mondayExtent, currentExtent);

        // Then
        assertThat(result).isFalse();
    }
}