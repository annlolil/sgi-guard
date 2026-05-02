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

import java.time.LocalDate;
import java.util.List;

@Service
public class ParentalLeaveService implements ParentalLeaveInterface {

    private final ParentalLeaveRepository parentalLeaveRepository;
    private final PersonRepository personRepository;
    private final SgiRuleService sgiRuleService;
    private final ShiftQueryService shiftQueryService;
    private final ParentalLeaveQueryService parentalLeaveQueryService;

    @Autowired
    public ParentalLeaveService(ParentalLeaveRepository parentalLeaveRepository, PersonRepository personRepository, SgiRuleService sgiRuleService, ShiftQueryService shiftQueryService, ParentalLeaveQueryService parentalLeaveQueryService) {
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.personRepository = personRepository;
        this.sgiRuleService = sgiRuleService;
        this.shiftQueryService = shiftQueryService;
        this.parentalLeaveQueryService = parentalLeaveQueryService;
    }

    public ParentalLeaveResponse addParentalLeave(ParentalLeaveRequest request) {

        Person person = personRepository.findById(request.getPersonId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        List<Shift> shifts = shiftQueryService.getShiftsForPersonInPeriod(
                request.getPersonId(),
                request.getDate().minusDays(4),
                request.getDate().plusDays(4));

        double fridayExtent = parentalLeaveQueryService.getSum(
                request.getPersonId(), sgiRuleService.getFriday(request.getDate()));
        double mondayExtent = parentalLeaveQueryService.getSum(
                request.getPersonId(), sgiRuleService.getMonday(request.getDate()));

        ParentalLeave temporaryLeave = ParentalLeave.builder()
                .date(request.getDate())
                .extent(request.getExtent())
                .build();

        String validWarning = sgiRuleService.validateParentalLeaveDay(
                temporaryLeave, shifts, fridayExtent, mondayExtent);

        if (validWarning != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validWarning);
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