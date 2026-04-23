package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.services.ParentalLeaveService;

@RestController
@RequestMapping("/parentalleave")
public class ParentalLeaveController {

    private final ParentalLeaveService parentalLeaveService;

    @Autowired
    public ParentalLeaveController(ParentalLeaveService parentalLeaveService) {
        this.parentalLeaveService = parentalLeaveService;
    }

    @PostMapping("/add")
    public ResponseEntity<ParentalLeave> addParentalLeave(@RequestBody ParentalLeaveRequest request) {
        return new ResponseEntity<>(parentalLeaveService.addParentalLeave(request), HttpStatus.CREATED);
    }
}
