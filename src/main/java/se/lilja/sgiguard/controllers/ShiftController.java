package se.lilja.sgiguard.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.dtos.ShiftResponse;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.services.ShiftService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ShiftController {

    private final ShiftService shiftService;
    private final ShiftRepository shiftRepository;

    @Autowired
    public ShiftController(ShiftService shiftService, ShiftRepository shiftRepository) {
        this.shiftService = shiftService;
        this.shiftRepository = shiftRepository;
    }

    @PostMapping("/addshift")
    public ResponseEntity<ShiftResponse> addShift(@Valid @RequestBody ShiftRequest shiftRequest) {
        return new ResponseEntity<>(shiftService.addShift(shiftRequest), HttpStatus.CREATED);
    }

//    @DeleteMapping("/deleteshifts")
//    public ResponseEntity<String> deleteShifts(@AuthenticationPrincipal User user,
//                                              @RequestParam LocalDate date) {
//        shiftService.deleteShiftsInWeek(user.getUsername(), date);
//        return new ResponseEntity<>("Shifts deleted", HttpStatus.OK);
//    }
}
