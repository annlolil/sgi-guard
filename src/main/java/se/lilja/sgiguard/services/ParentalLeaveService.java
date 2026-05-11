package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.dtos.ParentalLeaveResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.models.DailyWorkSummary;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;

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

        LocalDate requestDate = request.getDate();
        Long personId = person.getId();

        ParentalLeave newParentalLeave = ParentalLeave.builder()
                .person(person)
                .date(requestDate)
                .extent(request.getExtent()).build();

        // Check if current extent exceeds eventual existing extent
        double existingExtent = parentalLeaveRepository.findExtentsByDateAndPersonId(requestDate, personId);
        boolean validExtent = sgiRuleService.isExtentValid(newParentalLeave, existingExtent);
        if (!validExtent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Extent exceeds 1.0 days");
        }
        double totalNewExtent = existingExtent + request.getExtent();

        // Check if the requested date is a work free day and a weekend
        boolean workFreeDay = sgiRuleService.isWorkFreeDay(personId, requestDate);

        if(!workFreeDay) {
            DailyWorkSummary summary =
                    sgiCalculationService.calculateDailyWorkSummary(personId, requestDate);
            if(totalNewExtent > summary.getRecommendedExtent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Extent exceeds the gap to fill");
            }
        }

        boolean weekend = sgiRuleService.isWeekend(requestDate);
        if (weekend && workFreeDay) {

            boolean longLeave = sgiRuleService.is5DayFree(requestDate, personId);

            /* If not free for >= 5 days
             Check that weekend rules are fulfilled in terms of
             Extent on surrounding days are more or the same as weekend leaves extent */
            if(!longLeave) {
                LocalDate friday = requestDate.getDayOfWeek() == DayOfWeek.SATURDAY
                        ? requestDate.minusDays(1)
                        : requestDate.minusDays(2);

                LocalDate monday = requestDate.getDayOfWeek() == DayOfWeek.SUNDAY
                        ? requestDate.plusDays(1)
                        : requestDate.plusDays(2);

                double fridayExtent = parentalLeaveRepository.findExtentsByDateAndPersonId(friday, personId);
                double mondayExtent = parentalLeaveRepository.findExtentsByDateAndPersonId(monday, personId);

                boolean validWeekendExtent = sgiRuleService.isWeekendExtentValid(totalNewExtent, fridayExtent, mondayExtent);
                if (!validWeekendExtent) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weekend extent not valid");
                }
            }
        }
        ParentalLeave savedLeave = parentalLeaveRepository.save(newParentalLeave);

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
