package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.services.EmploymentService;

@RestController
@RequestMapping("/employment")
public class EmploymentController {

    private final EmploymentService employmentService;

    @Autowired
    public EmploymentController(EmploymentService employmentService) {
        this.employmentService = employmentService;
    }
    // Is currently returning a workcondition object with shifts which I dont want to have. Remove shifts from the return.
    @PostMapping("/addemployment")
    public ResponseEntity<Employment> addEmployment(@RequestBody Employment employment, @RequestParam Long personId) {
        return new ResponseEntity<>(employmentService.addEmployment(employment, personId), HttpStatus.CREATED);
    }
}
