package se.lilja.sgiguard.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.*;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.models.SgiStatus;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.services.ParentalLeaveService;
import se.lilja.sgiguard.services.PersonService;
import se.lilja.sgiguard.services.SgiCalculationService;
import se.lilja.sgiguard.services.ShiftService;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class WebController {

    private final SgiCalculationService sgiCalculationService;
    private final ParentalLeaveService parentalLeaveService;
    private final PersonService personService;
    private final ShiftService shiftService;

    @Autowired
    public WebController(final SgiCalculationService sgiCalculationService, ParentalLeaveService parentalLeaveService, PersonService personService, ShiftService shiftService) {
        this.sgiCalculationService = sgiCalculationService;
        this.parentalLeaveService = parentalLeaveService;
        this.personService = personService;
        this.shiftService = shiftService;
    }

    @GetMapping("/")
    public String home(

            @AuthenticationPrincipal User user,
            Model model
    ) {

        Person person = personService.getByPersonalNumber(user.getUsername());

        model.addAttribute("person", person);

        return "index";
    }

    @PostMapping("/analyzeweek")
    public String analyzeWeek(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate date,
            Model model) {

        try {
            Person person = personService.getByPersonalNumber(user.getUsername());

            SgiWeeklyAnalysisResponse response =
                    sgiCalculationService.analyzeWeek(person.getPersonalNumber(), date);

            boolean hasRisk =
                    response.getDailyAnalyses()
                            .stream()
                            .anyMatch(day ->
                                    day.getStatus() == SgiStatus.AT_RISK);

            model.addAttribute("weeklyanalyses", response);
            model.addAttribute("hasRisk", hasRisk);
            model.addAttribute("person", person);

            return "weeklyresult";
        }
        catch (ResponseStatusException e) {
            Person person =
                    personService.getByPersonalNumber(
                            user.getUsername());

            model.addAttribute("person", person);
            model.addAttribute("error", e.getReason());
            return "index";
        }
    }

    @PostMapping("/parental-leave")
    public String addParentalLeave(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate date,
            @RequestParam Double extent,
            Model model) {

        try {

            ParentalLeaveRequest request =
                    new ParentalLeaveRequest();

            request.setPersonalNumber(user.getUsername());
            request.setDate(date);
            request.setExtent(extent);

            parentalLeaveService.addParentalLeave(request);

            return "redirect:/?success=parentalLeaveAdded";
        }
        catch (ResponseStatusException e) {
            Person person =
                    personService.getByPersonalNumber(
                            user.getUsername());

            model.addAttribute("person", person);
            model.addAttribute("leaveError", e.getReason());
            return "index";
        }
    }

    @PostMapping("/person")
    public String addPerson(
            @RequestParam String personalNumber,
            @RequestParam String firstName,
            @RequestParam String lastName,
            Model model) {

        try {
            PersonRequest request =
                    new PersonRequest();

            request.setPersonalNumber(personalNumber);
            request.setFirstName(firstName);
            request.setLastName(lastName);

            PersonResponse response = personService.addPerson(request);

            model.addAttribute("person", response);

            return "redirect:/?success=personAdded";
        }
        catch (ResponseStatusException e) {
            model.addAttribute("personError", e.getReason());
            return "login";
        }
    }

    @PostMapping("/shift")
    public String addShift(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate startDate,
            @RequestParam LocalTime startTime,
            @RequestParam LocalDate endDate,
            @RequestParam LocalTime endTime,
            @RequestParam Integer breakMinutes,
            @RequestParam String type,
            Model model) {

        try {
            Person person =
                personService.getByPersonalNumber(
                        user.getUsername());

            ShiftRequest request =
                    new ShiftRequest();

            request.setPersonalNumber(person.getPersonalNumber());
            request.setStartDate(startDate);
            request.setStartTime(startTime);
            request.setEndDate(endDate);
            request.setEndTime(endTime);
            request.setBreakMinutes(breakMinutes);
            request.setType(type);

            ShiftResponse response = shiftService.addShift(request);

            model.addAttribute("shift", response);

            return "redirect:/?success=shiftAdded";
        }
        catch (ResponseStatusException e) {
            Person person =
                    personService.getByPersonalNumber(
                            user.getUsername());
            model.addAttribute("person", person);
            model.addAttribute("shiftError", e.getReason());
            return "index";
        }
    }
}
