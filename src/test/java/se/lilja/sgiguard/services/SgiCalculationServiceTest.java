package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class SgiCalculationServiceTest {

    @Mock
    ShiftRepository shiftRepository;

    @InjectMocks
    SgiCalculationService sgiCalculationService;

    private final Shift shift = new Shift();

    @Test
    void identifyMainDay() {
        // Given
        shift.setShiftStart(LocalDate.now().atTime(LocalTime.of(20, 0)));
        shift.setShiftEnd(LocalDate.now().plusDays(1).atTime(LocalTime.of(6, 0)));

        // When
        LocalDate result = sgiCalculationService.identifyMainDay(shift);

        assertThat(result).isEqualTo(LocalDate.now().plusDays(1));

    }
}