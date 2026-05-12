package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.dtos.ShiftResponse;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.services.ShiftService;

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
    @PostMapping("/addshift")
    public ResponseEntity<ShiftResponse> addShift(@RequestBody ShiftRequest shiftRequest) {
        return new ResponseEntity<>(shiftService.addShift(shiftRequest), HttpStatus.CREATED);
    }

//    // An endpoint that gets a specific persons all saved shifts.
//    @GetMapping("/getshifts")
//    public ResponseEntity<List<Shift>> getShifts(Long personId) {
//        return ResponseEntity.ok(shiftService.getShifts(personId));
//    }

    @DeleteMapping("/deleteshift/{id}")
    public ResponseEntity<String> deleteShift(@PathVariable Long id) {
        shiftRepository.deleteById(id);
        return ResponseEntity.ok("Shift deleted");
    }
}
