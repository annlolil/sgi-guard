package se.lilja.sgiguard.controllers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.lilja.sgiguard.dtos.SgiDailyAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiPeriodAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiWeeklyAnalysisResponse;
import se.lilja.sgiguard.services.SgiCalculationService;

import java.time.LocalDate;

@RestController
@RequestMapping("/sgi")
public class SgiCalculationController {

    private final SgiCalculationService sgiCalculationService;

    @Autowired
    public SgiCalculationController(SgiCalculationService sgiCalculationService) {
        this.sgiCalculationService = sgiCalculationService;
    }

//    @GetMapping("/analyzeweek")
//    public ResponseEntity<SgiWeeklyAnalysisResponse> analyzeWeek(
//            @RequestParam Long personId,
//            @RequestParam LocalDate weekStart) {
//
//        SgiWeeklyAnalysisResponse response = sgiCalculationService.analyzeWeek(personId, weekStart);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/analyzeday")
    public ResponseEntity<SgiDailyAnalysisResponse> analyzeDay(
            @RequestParam String personalNumber,
            @RequestParam LocalDate date) {

        SgiDailyAnalysisResponse response = sgiCalculationService.analyzeDay(personalNumber, date);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
