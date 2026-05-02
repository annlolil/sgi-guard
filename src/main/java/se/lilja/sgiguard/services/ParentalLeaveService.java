package se.lilja.sgiguard.services;

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

    public ParentalLeaveResponse addParentalLeave(ParentalLeaveRequest request) {

        Person person = personRepository.findById(request.getPersonId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        LocalDate startDate = request.getDate().minusDays(4);
        LocalDate endDate = request.getDate().plusDays(4);

        List<Shift> shifts = sgiCalculationService.getShiftsForPersonInPeriod(
                request.getPersonId(),
                startDate.minusDays(4),
                endDate.plusDays(4));

        // Check if a person is free from work for 5 days in a row.
        boolean isLongLeave = sgiRuleService.is5DayFree(request.getDate(), shifts);

        LocalDate requestDate = request.getDate();
        if (sgiRuleService.isWeekend(request.getDate()) && !isLongLeave) {
            LocalDate friday = requestDate.getDayOfWeek().getValue() == 6
                    ? requestDate.minusDays(1)
                    : requestDate.minusDays(2);

            LocalDate monday = requestDate.getDayOfWeek().getValue() == 6
                    ? requestDate.plusDays(2)
                    : requestDate.plusDays(1);

            double fridayExtent = parentalLeaveRepository.getLeaveExtentOnDay(request.getPersonId(), friday);
            double mondayExtent = parentalLeaveRepository.getLeaveExtentOnDay(request.getPersonId(), monday);

            boolean valid = sgiRuleService.isWeekendClaimValid(request.getDate(), fridayExtent, mondayExtent, request.getExtent());

            if (!valid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Weekend rule violation: Weekend claims require connecting weekday leave of at least "
                                + request.getExtent());
            }
        }
        ParentalLeave parentalLeave = ParentalLeave.builder()
                        .person(person)
                        .date(request.getDate())
                        .extent(request.getExtent()).build();

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
