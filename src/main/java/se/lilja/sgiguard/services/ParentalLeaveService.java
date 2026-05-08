package se.lilja.sgiguard.services;

import org.hibernate.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.dtos.ParentalLeaveResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ParentalLeaveService implements ParentalLeaveInterface {

    private final ParentalLeaveRepository parentalLeaveRepository;
    private final PersonRepository personRepository;
    private final SgiRuleService sgiRuleService;
    private final SgiCalculationService sgiCalculationService;

    @Autowired
    public ParentalLeaveService(ParentalLeaveRepository parentalLeaveRepository, PersonRepository personRepository, SgiRuleService sgiRuleService, SgiCalculationService sgiCalculationService) {
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.personRepository = personRepository;
        this.sgiRuleService = sgiRuleService;
        this.sgiCalculationService = sgiCalculationService;
    }

    /* When adding new parental leave
    1. Get weeks baseline workhours
    2. Get weeks actual workhours
    3. Get already registered parental leave
    4. Simulate new leave
    5. Check total to validate it is okay
     */

    public ParentalLeaveResponse addParentalLeave(ParentalLeaveRequest request) {

        Person person = personRepository.findById(request.getPersonId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        ParentalLeave parentalLeave = ParentalLeave.builder()
                .person(person)
                .date(request.getDate())
                .extent(request.getExtent()).build();

        LocalDate requestDate = request.getDate();

        List<Shift> shifts = sgiCalculationService.getShiftsForPersonInPeriod(
                request.getPersonId(),
                requestDate.minusDays(4),
                requestDate.plusDays(4));

        List<ParentalLeave> existingLeaves = parentalLeaveRepository.findByPersonIdAndDate(
                request.getPersonId(), request.getDate());

        if (sgiRuleService.isWeekend(request.getDate())) {

            double fridayExtent;
            double mondayExtent;

            LocalDate friday = requestDate.getDayOfWeek() == DayOfWeek.SATURDAY
                    ? requestDate.minusDays(1)
                    : requestDate.minusDays(2);

            LocalDate monday = requestDate.getDayOfWeek() == DayOfWeek.SUNDAY
                    ? requestDate.plusDays(1)
                    : requestDate.plusDays(2);

            fridayExtent = parentalLeaveRepository.getLeaveExtentOnDay(
                    request.getPersonId(), friday);

            mondayExtent = parentalLeaveRepository.getLeaveExtentOnDay(
                    request.getPersonId(), monday);

            String validationError = sgiRuleService.validateParentalLeaveDay(
                    parentalLeave, shifts, existingLeaves, fridayExtent, mondayExtent);

            if (validationError != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationError);
            }
        }
        ParentalLeave savedLeave = parentalLeaveRepository.save(parentalLeave);

        return new ParentalLeaveResponse(savedLeave.getDate(), savedLeave.getExtent());
    }

    public String deleteParentalLeave(Long id) {

        ParentalLeave parentalLeave = parentalLeaveRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parental leave not found")
        );

        parentalLeaveRepository.delete(parentalLeave);
        return "Parental leave deleted on " + parentalLeave.getDate() + ".";
    }

}
