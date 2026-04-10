package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class SgiCalculationServiceTest {

    private final ShiftRepository shiftRepository = Mockito.mock(ShiftRepository.class);
    private final SgiCalculationService sgiCalculationService = new SgiCalculationService(shiftRepository);
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
    void weeklyTargetHours_ShouldReturnWeeklyTargetHours() {
        // Given
        WorkCondition workCondition = new WorkCondition();
        workCondition.setId(1L);
        workCondition.setOriginalEmploymentRate(100.0);
        workCondition.setCurrentEmploymentRate(85.0);
        workCondition.setOriginalWorkingHours(34.2);
        workCondition.setValidFrom(LocalDate.now());

        // When
        Double result = sgiCalculationService.weeklyTargetHours(workCondition);

        // Then
        assertThat(result).isEqualTo(29.07);
    }
}