package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.dtos.ShiftDTO;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.services.SgiCalculationService;
import se.lilja.sgiguard.services.ShiftService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Change to Controller later on
@RestController
@RequestMapping("/shift")
public class ShiftController {

    private final ShiftService shiftService;
    private final SgiCalculationService sgiCalculationService;

    @Autowired
    public ShiftController(ShiftService shiftService, SgiCalculationService sgiCalculationService) {
        this.shiftService = shiftService;
        this.sgiCalculationService = sgiCalculationService;
    }

    // An endpoint where a user adds a shift. The endpoint looks at the current users id and saves it to the shift.
    @PostMapping("/addshift")
    public ResponseEntity<Shift> addShift(@RequestBody ShiftDTO shiftDTO,
                                          @RequestParam Long personId, // Change later do @Authentication principal if I have the time
                                          @RequestParam Long workConditionId) {
        return new ResponseEntity<>(shiftService.addShift(shiftDTO, personId, workConditionId), HttpStatus.CREATED);
    }

    // An endpoint that gets a specific persons all saved shifts.
    @GetMapping("/getshifts")
    public ResponseEntity<List<Shift>> getShifts(Long personId) {
        return ResponseEntity.ok(shiftService.getShifts(personId));
    }

    // An endpoint that gets a list of shifts for a specific person in a certain period
    @GetMapping("/getshiftsforperiod")
    public ResponseEntity<List<Shift>> getShiftsForPeriod(@RequestParam  Long personId,
                                                          @RequestParam LocalDate from,
                                                          @RequestParam LocalDate to) {
        return ResponseEntity.ok(sgiCalculationService.getShiftsForPersonInPeriod(personId, from, to));
    }
}
