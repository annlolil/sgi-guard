package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.services.ShiftService;

import java.security.Principal;
import java.util.List;

// Change to Controller later on
@RestController
@RequestMapping("/shift")
public class ShiftController {

    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    // An endpoint where a user adds a shift. The endpoint looks at the current users id and saves it to the shift.
    @PostMapping("/addshift")
    public ResponseEntity<Shift> addShift(@RequestBody Shift shift, @RequestParam Long personId) {
        return new ResponseEntity<>(shiftService.addShift(shift, personId), HttpStatus.CREATED);
    }

    // An endpoint that gets a specific persons all saved shifts.
    @GetMapping("/getshifts")
    public ResponseEntity<List<Shift>> getShifts(Person person) {
        return ResponseEntity.ok(shiftService.getShifts(person));
    }

    // An endpoint that gets all shifts saved in the database.
    @GetMapping("/getallshifts")
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }
}
