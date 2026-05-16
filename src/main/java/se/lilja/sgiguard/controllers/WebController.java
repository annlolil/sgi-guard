package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.*;
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
    public String home(Model model) {

        model.addAttribute("message", "SGIGuard");

        return "index";
    }

//    @PostMapping("/analyze")
//    public String analyze(
//            @RequestParam String personalNumber,
//            @RequestParam LocalDate date,
//            Model model) {
//
//        SgiDailyAnalysisResponse response =
//                sgiCalculationService.analyzeDay(personalNumber, date);
//
//        model.addAttribute("analysis", response);
//
//        return "result";
//    }

    @PostMapping("/analyzeweek")
    public String analyzeweek(
            @RequestParam String personalNumber,
            @RequestParam LocalDate date,
            Model model) {

        try {
            SgiWeeklyAnalysisResponse response =
                    sgiCalculationService.analyzeWeek(personalNumber, date);

            model.addAttribute("weeklyanalyses", response);

            return "weeklyresult";
        }
        catch (ResponseStatusException e) {
            model.addAttribute("error", e.getReason());
            return "index";
        }
    }

    @PostMapping("/parental-leave")
    public String addParentalLeave(
            @RequestParam String personalNumber,
            @RequestParam LocalDate date,
            @RequestParam Double extent,
            Model model) {

        try {
            ParentalLeaveRequest request =
                    new ParentalLeaveRequest();

            request.setPersonalNumber(personalNumber);
            request.setDate(date);
            request.setExtent(extent);

            ParentalLeaveResponse response =
                    parentalLeaveService.addParentalLeave(request);

            model.addAttribute(
                    "leave",
                    response);

            return "redirect:/?success=parentalLeaveAdded";
        }
        catch (ResponseStatusException e) {
            model.addAttribute("error", e.getReason());
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
            model.addAttribute("error", e.getReason());
            return "index";
        }
    }

    @PostMapping("/shift")
    public String addShift(
            @RequestParam String personalNumber,
            @RequestParam LocalDate startDate,
            @RequestParam LocalTime startTime,
            @RequestParam LocalDate endDate,
            @RequestParam LocalTime endTime,
            @RequestParam Integer breakMinutes,
            @RequestParam String type,
            Model model) {

        try {
            ShiftRequest request =
                    new ShiftRequest();

            request.setPersonalNumber(personalNumber);
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
            model.addAttribute("error", e.getReason());
            return "index";
        }
    }
}
