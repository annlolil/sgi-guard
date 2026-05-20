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
import se.lilja.sgiguard.models.Warning;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;
import se.lilja.sgiguard.repositories.PersonRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

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

    public ParentalLeaveResponse addParentalLeave(Person person, ParentalLeaveRequest request) {

        LocalDate requestDate = request.getDate();
        Long personId = person.getId();

        if(!VALID_EXTENTS.contains(request.getExtent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Warning.INVALID_EXTENT.getMessage());
        }

        ParentalLeave newParentalLeave = ParentalLeave.builder()
                .person(person)
                .date(requestDate)
                .extent(request.getExtent()).build();

        // Check if new and existing extent exceeds 1.0 days
        double existingExtent = parentalLeaveRepository.findExtentsByDateAndPersonId(requestDate, personId);
        boolean validExtent = sgiRuleService.isExtentValid(newParentalLeave, existingExtent);
        if (!validExtent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Warning.EXTENT_EXCEEDS_DAY.getMessage());
        }
        double totalNewExtent = existingExtent + request.getExtent();

        // Check if the requested date is a work free day and a weekend
        boolean workFreeDay = sgiRuleService.isWorkFreeDay(personId, requestDate);

        if(!workFreeDay) {
            DailyWorkSummary summary =
                    sgiCalculationService.calculateDailyWorkSummary(personId, requestDate);
            if(totalNewExtent > summary.getRecommendedExtent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Warning.EXTENT_EXCEEDS_GAP.getMessage());
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

                boolean validWeekendExtent = sgiRuleService.isWeekendExtentValid(fridayExtent, mondayExtent, totalNewExtent);
                if (!validWeekendExtent) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Warning.WEEKEND_RULE.getMessage());
                }
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Warning.WEEK_SCHEDULE_MISSING.getMessage());
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

    public void deleteParentalLeavesInWeek(Long personId, LocalDate start) {

        Person person = personRepository.findById(personId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        LocalDate weekStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(7);

        List<ParentalLeave> parentalLeavesToDelete = parentalLeaveRepository
                .findByPersonIdAndDateBetween(person.getId(), weekStart, weekEnd);

        parentalLeaveRepository.deleteAll(parentalLeavesToDelete);
    }



    // Extents that represents parts of a parental leave day
    public static final List<Double> VALID_EXTENTS =
            List.of(0.125, 0.25, 0.5, 0.75, 1.0);
}
