package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.lilja.sgiguard.dtos.SgiPeriodAnalysisResponse;
import se.lilja.sgiguard.dtos.SgiStatusResponse;
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

    @GetMapping("/analyzeweek")
    public ResponseEntity<SgiStatusResponse> analyzeWeek(
            @RequestParam Long personId,
            @RequestParam LocalDate weekStart) {

        SgiStatusResponse response = sgiCalculationService.calculateSgiStatus(personId, weekStart);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analyzeperiod")
    public ResponseEntity<SgiPeriodAnalysisResponse> analyzePeriod(
            @RequestParam Long personId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        SgiPeriodAnalysisResponse response = sgiCalculationService.analyzePeriod(personId, from, to);
        return ResponseEntity.ok(response);
    }
}
