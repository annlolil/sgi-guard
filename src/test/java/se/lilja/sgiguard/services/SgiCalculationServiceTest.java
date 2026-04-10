package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        Employment employment = new Employment();
        employment.setId(1L);
        employment.setOriginalEmploymentRate(100.0);
        employment.setCurrentEmploymentRate(85.0);
        employment.setOriginalWorkingHours(34.2);
        employment.setValidFrom(LocalDate.now());

        // When
        Double result = sgiCalculationService.calculateCurrentWeeklyHours(employment);

        // Then
        assertThat(result).isEqualTo(29.07);
    }
}