package se.lilja.sgiguard.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lilja.sgiguard.dtos.SgiDailyAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SgiCalculationServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock // Used by @InjectMocks
    ParentalLeaveRepository parentalLeaveRepository;

    @InjectMocks
    private SgiCalculationService sgiCalculationService;

    Person person = new Person();
    LocalDate date = LocalDate.now();
    String personalNumber;
    Long personId;

    @BeforeEach
    void setUp() {
        personalNumber = "20000101-1111";
        personId = 1L;
        date = LocalDate.of(2024, 4, 1);

        person.setId(personId);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setPersonalNumber(personalNumber);
    }

    @Test
    void analyzeDay_ShouldReturnAtRisk_WhenParentalLeaveIsMissing() {

        // Given
        /* Baseline shift with 8 hour work and
           actual shift with 6 hour work gives 2 hour missing and recommended extent 0,25 h*/
        Shift baselineShift = Shift.builder()
                .shiftStart(date.atTime(8,0))
                .shiftEnd(date.atTime(17,0))
                .breakMinutes(60)
                .type(ShiftType.BASELINE)
                .build();

        Shift actualShift = Shift.builder()
                .shiftStart(date.atTime(8,0))
                .shiftEnd(date.atTime(15,0))
                .breakMinutes(60)
                .type(ShiftType.ACTUAL)
                .build();

        when(personRepository.findPersonByPersonalNumber(personalNumber))
                .thenReturn(Optional.ofNullable(person));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        when(shiftRepository.findOverlappingShifts(
                personId,
                dayStart,
                dayEnd))
        .thenReturn(List.of(baselineShift, actualShift));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personalNumber, date);

        // Then
        assertThat(response.getStatus())
                .isEqualTo(SgiStatus.AT_RISK);

        assertThat(response.getRecommendedExtent())
                .isEqualTo(0.25);
    }

    @Test
    void analyzeDay_ShouldReturnZero_WhenNoBaselineShiftExists() {

        // Given
        when(personRepository.findPersonByPersonalNumber(personalNumber)).
                thenReturn(Optional.ofNullable(person));

        // When
        SgiDailyAnalysisResponse response =
                sgiCalculationService.analyzeDay(personalNumber, date);

        // Then
        assertThat(response.getBaselineHours()).isEqualTo("0 h");
    }

    @Test
    void analyzeWeek_ShouldReturn_WeeklyAnalysisResponse() {

        // Given
        when(personRepository.findPersonByPersonalNumber(personalNumber))
                .thenReturn(Optional.ofNullable(person));

        // When
        SgiWeeklyAnalysisResponse response =
                sgiCalculationService.analyzeWeek(personalNumber, date);

        // Then
        assertThat(response.getWeeklyNumber())
                .isEqualTo(14);

        assertThat(response.getDailyAnalyses().getFirst().getStatus())
                .isEqualTo(SgiStatus.PROTECTED);
    }
}