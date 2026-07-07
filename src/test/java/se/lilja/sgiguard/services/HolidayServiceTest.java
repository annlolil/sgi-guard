package se.lilja.sgiguard.services;

import de.focus_shift.jollyday.core.HolidayManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayManager holidayManager;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    void isHoliday_ShouldReturnTrue_WhenDateIsAPublicHoliday() {

        // Given - 2024-04-01 is a holiday, easter.
        LocalDate date = LocalDate.of(2024, 4, 1);

        // When
        boolean result = holidayService.isHoliday(date);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isHoliday_ShouldReturnFalse_WhenDateIsNotAPublicHoliday() {

        // Given - 2024-04-05 a regular friday
        LocalDate date = LocalDate.of(2024, 4, 5);

        // When
        boolean result = holidayService.isHoliday(date);

        // Then
        assertThat(result).isFalse();
    }
}