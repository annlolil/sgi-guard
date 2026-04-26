package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
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
    private final ShiftRepository shiftRepository;

    @Autowired
    public ParentalLeaveService(ParentalLeaveRepository parentalLeaveRepository, PersonRepository personRepository, SgiRuleService sgiRuleService, ShiftRepository shiftRepository) {
        this.parentalLeaveRepository = parentalLeaveRepository;
        this.personRepository = personRepository;
        this.sgiRuleService = sgiRuleService;
        this.shiftRepository = shiftRepository;
    }

    public ParentalLeave addParentalLeave(ParentalLeaveRequest request) {

        Person person = personRepository.findById(request.getPersonId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        List<Shift> shifts = shiftRepository.findShiftByPersonId(person.getId());
        boolean isLongLeave = sgiRuleService.is5DayFree(request.getDate(), shifts);

        if (sgiRuleService.isWeekend(request.getDate()) && !isLongLeave) {
            double fridayExtent = getLeaveExtentOnDay(request.getPersonId(), request.getDate());
            double mondayExtent = getLeaveExtentOnDay(request.getPersonId(), request.getDate());

            boolean valid = sgiRuleService.isWeekendClaimValid(request.getDate(), fridayExtent, mondayExtent, request.getExtent());

            if (!valid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Weekend rule violation: Weekend claims require connecting weekday leave of at least " + request.getExtent());
            }
        }
        ParentalLeave parentalLeave = new ParentalLeave();
        parentalLeave.setPerson(person);
        parentalLeave.setDate(request.getDate());
        parentalLeave.setExtent(request.getExtent());
        return parentalLeaveRepository.save(parentalLeave);
    }

    public String deleteParentalLeave(Long id) {

        ParentalLeave parentalLeave = parentalLeaveRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parental leave not found")
        );

        parentalLeaveRepository.delete(parentalLeave);
        return ("Parental leave deleted on " + parentalLeave.getDate()) + " .";
    }

    public double getLeaveExtentOnDay(Long personId, LocalDate date) {
        return parentalLeaveRepository.findByPersonIdAndDate(personId, date)
                .stream()
                .mapToDouble(ParentalLeave::getExtent)
                .sum();
    }
}
