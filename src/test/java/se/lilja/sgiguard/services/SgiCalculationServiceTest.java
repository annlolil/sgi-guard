package se.lilja.sgiguard.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
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

    // Testing the scenario when a shifts start in a period but ends after the period
//    @Test
//    void getShiftsForPersonInPeriod_ShouldReturnShifts_WhenShiftsAreStartingInPeriodAndNotEndingInPeriod() {
//
//        // Given
//        //Period to list shifts from
//        LocalDate from = LocalDate.of(2024, 10, 1);
//        LocalDate to = from.plusDays(30);
//
//        //Shift start and shift end
//        LocalDate start = LocalDate.of(2024, 10, 31);
//        LocalDate end = LocalDate.of(2024, 11, 1);
//        shift.setShiftStart(start.atTime(21, 0));
//        shift.setShiftEnd(end.atTime(7, 0));
//
//        //Person
//        Person person = new Person();
//        person.setId(1L);
//        shift.setPerson(person);
//
//        //When
//        List<Shift> shifts = sgiCalculationService.getShiftsForPersonInPeriod(1L, from, to);
//    }
}