package se.lilja.sgiguard.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SgiRuleServiceTest {

    private SgiRuleService sgiRuleService;
    private Shift shift1;
    private Shift shift2;


    @BeforeEach
    void setUp() {
        sgiRuleService = new SgiRuleService();
        shift1 = new Shift();
        shift2 = new Shift();
    }

    @Test
    void identifyMainDay_ShouldReturnSecondDay_WhenMoreHoursOnSecondDay() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);
        LocalDate nextDay = startDay.plusDays(1);

        // 20:00 to 06:00 (4h on day 1, 6h on day 2)
        shift1.setShiftStart(startDay.atTime(20, 0));
        shift1.setShiftEnd(nextDay.atTime(6, 0));

        // When
        LocalDate result = sgiRuleService.identifyMainDay(shift1);

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
        LocalDate result = sgiRuleService.identifyMainDay(shift1);

        // Then
        assertThat(result).isEqualTo(startDay);
    }

    @Test
    void identifyMainDay_ShouldReturnFirstDay_WhenFirstAndSecondDayAreTheSame() {
        // Given
        LocalDate startDay = LocalDate.of(2024, 10, 15);

        // 08:00 to 17:00 on the same day
        shift1.setShiftStart(startDay.atTime(8, 0));
        shift1.setShiftEnd(startDay.atTime(17, 0));

        // When
        LocalDate result = sgiRuleService.identifyMainDay(shift1);

        assertThat(result).isEqualTo(startDay);
    }

    @Test
    void is5DayFree_shouldReturnFalse_whenPersonIs4DaysFree() {

        // Given
        Person person = new Person();
        person.setId(1L);
        // Shift 1 has main day 2 januari, so 2 januari is not free
        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        // Shift 2 has main day 7 januari so 3, 4, 5 and 6 januari is free (4 days), but not 7 januari
        shift2.setShiftStart(LocalDateTime.of(2024, 1, 6, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 7, 6, 0));

        List<Shift> shifts = Arrays.asList(shift1, shift2);

        // When
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 5), shifts);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenPersonIs5DayFreeAfterShift() {

        // Given
        Person person = new Person();
        person.setId(1L);
        // Shift 1 has main day 2 januari, so 2 januari is not free
        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        // Shift 2 has main day 8 januari so 3, 4, 5, 6 and 7 januari is free (5 days), but not 8 januari
        shift2.setShiftStart(LocalDateTime.of(2024, 1, 7, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 8, 6, 0));

        List<Shift> shifts = Arrays.asList(shift1, shift2);

        // When
        // Looking at the first day of the free period
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 3), shifts);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenPersonIs5DayFreeBeforeShift() {

        // Given
        Person person = new Person();
        person.setId(1L);
        // Shift 1 has main day 2 januari, so 2 januari is not free
        shift1.setShiftStart(LocalDateTime.of(2024, 1, 1, 20, 0));
        shift1.setShiftEnd(LocalDateTime.of(2024, 1, 2, 6, 0));
        // Shift 2 has main day 8 januari so 3, 4, 5, 6 and 7 januari is free (5 days), but not 8 januari
        shift2.setShiftStart(LocalDateTime.of(2024, 1, 7, 20, 0));
        shift2.setShiftEnd(LocalDateTime.of(2024, 1, 8, 6, 0));

        List<Shift> shifts = Arrays.asList(shift1, shift2);

        // When
        // Looking at the last day of the free period
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 7), shifts);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenNoShiftsAtAll() {

        // Given
        List<Shift> shifts = Collections.emptyList();

        // When
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 7), shifts);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isWeekendClaimValid_ShouldReturnTrue_WhenConnectingDayIsClaimed() {

        // Given
        // A saturday
        LocalDate dayOfWeek = LocalDate.of(2024, 1, 6);

        // When
        boolean result = sgiRuleService.isWeekendClaimValid(dayOfWeek, 1.0, 0, 1.0);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isWeekendClaimValid_ShouldReturnTrue_WhenDateIsNotAWeekendDay() {

        // Given
        // A monday
        LocalDate dayOfWeek = LocalDate.of(2024, 1, 1);

        // When
        boolean result = sgiRuleService.isWeekendClaimValid(dayOfWeek, 0, 0, 1);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isWeekendClaimValid_ShouldReturnTrue_WhenExtentOnMondayIsMoreThanCurrentDay() {

        // Given
        // A sunday
        LocalDate dayOfWeek = LocalDate.of(2024, 1, 7);

        // When
        boolean result = sgiRuleService.isWeekendClaimValid(dayOfWeek, 0, 1, 0.5);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isWeekendClaimValid_ShouldReturnFalse_WhenExtentIsLessThanCurrentDay() {

        // Given
        // A sunday
        LocalDate dayOfWeek = LocalDate.of(2024, 1, 7);

        // When
        boolean result = sgiRuleService.isWeekendClaimValid(dayOfWeek, 0, 0.5, 1);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isWeekendClaimValid_ShouldReturnFalse_WhenExtentOnFridayIsLessThanCurrentDay() {

        // Given
        // A sunday
        LocalDate dayOfWeek = LocalDate.of(2024, 1, 7);

        // When
        boolean result = sgiRuleService.isWeekendClaimValid(dayOfWeek, 0.5, 0, 1);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void validateParentalLeaveDay_ShouldReturnNull_WhenDateIsNotWeekendDay() {

        // Given
        ParentalLeave parentalLeave = new ParentalLeave();
        parentalLeave.setDate(LocalDate.of(2024, 1, 8));

        // When
        String result = sgiRuleService.validateParentalLeaveDay(
                parentalLeave,
                Collections.emptyList(),
                0,
                0);

        assertThat(result).isNull();
    }
}