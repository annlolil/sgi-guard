package se.lilja.sgiguard.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 5), 1L);

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
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 3), 1L);

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
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 7), 1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void is5DayFree_shouldReturnTrue_whenNoShiftsAtAll() {

        // Given
        List<Shift> shifts = Collections.emptyList();

        // When
        boolean result = sgiRuleService.is5DayFree(LocalDate.of(2024, 1, 7), 1L);

        // Then
        assertThat(result).isTrue();
    }
}