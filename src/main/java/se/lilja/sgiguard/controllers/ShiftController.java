package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.dtos.ShiftDTO;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.services.ShiftService;

import java.time.LocalDate;
import java.util.List;

// Change to Controller later on
@RestController
@RequestMapping("/shift")
public class ShiftController {

    private final ShiftService shiftService;
    private final ShiftRepository shiftRepository;

    @Autowired
    public ShiftController(ShiftService shiftService, ShiftRepository shiftRepository) {
        this.shiftService = shiftService;
        this.shiftRepository = shiftRepository;
    }

    // An endpoint where a user adds a shift. The endpoint looks at the current users id and saves it to the shift.
    @PostMapping("/addshift/{personId}")
    public ResponseEntity<Shift> addShift(@RequestBody ShiftDTO shiftDTO,
                                          @PathVariable Long personId) { // Change later do @Authentication principal if I have the time)
        return new ResponseEntity<>(shiftService.addShift(shiftDTO, personId), HttpStatus.CREATED);
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
        return ResponseEntity.ok(shiftRepository.getShiftsForPersonInPeriod(personId, from, to));
    }

    @DeleteMapping("/deleteshift/{id}")
    public ResponseEntity<String> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.ok(shiftRepository.deleteById(id));
    }
}
