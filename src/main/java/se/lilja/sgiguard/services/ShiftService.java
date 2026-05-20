package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.dtos.ShiftResponse;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ShiftService implements ShiftServiceInterface {

    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository,
                        PersonRepository personRepository) {
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
    }

    @Override
    public ShiftResponse addShift(Person person, ShiftRequest request) {
        // Get the person that is logged in and connect it to the shift that's being saved

        // Fetch start and end time for the shift and validate that
        // shifts start date and time is before end date and time
        // shift is not more than 24 hours long
        LocalDateTime startDateTime = LocalDateTime.of(request.getStartDate(), request.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.of(request.getEndDate(), request.getEndTime());


        int breakMinutes = request.getBreakMinutes();
        LocalDateTime newEndTime = endDateTime.minus(Duration.ofMinutes(breakMinutes));

        validateShiftDuration(startDateTime, newEndTime);

        // Shift type defaults to ACTUAL
        ShiftType type = ShiftType.ACTUAL;
        // Convert requested shift type from string to enum
        if(request.getType().startsWith("b") || request.getType().startsWith("B")) {
            type = ShiftType.BASELINE;
        }

        Shift newShift = Shift.builder()
                .shiftStart(startDateTime)
                .shiftEnd(endDateTime)
                .person(person)
                .breakMinutes(breakMinutes)
                .type(type).build();

        Shift savedShift = shiftRepository.save(newShift);

        return new ShiftResponse(savedShift.getShiftStart(), savedShift.getShiftEnd(), savedShift.getType());
    }

    private void validateShiftDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);

        if (duration.isNegative()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slutdatum kan inte vara före startdatum");
        }

        if (duration.toHours() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ett skift kan inte vara mindre än 1 timme");
        }

        if (duration.toHours() > 24) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ett skift kan inte vara mer än 24 timmar");
        }
    }

    public void deleteShiftsInWeek(Long personId, LocalDate start) {

        Person person = personRepository.findById(personId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        LocalDate weekStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(7);

        List<Shift> shiftsToDelete = shiftRepository.findOverlappingShifts(
                person.getId(),
                weekStart.atStartOfDay(),
                weekEnd.atStartOfDay());

        shiftRepository.deleteAll(shiftsToDelete);
    }
}
