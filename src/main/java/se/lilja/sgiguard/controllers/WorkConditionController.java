package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.services.EmploymentService;

@RestController
@RequestMapping("/workcondition")
public class WorkConditionController {

    private final EmploymentService workConditionService;

    @Autowired
    public WorkConditionController(EmploymentService workConditionService) {
        this.workConditionService = workConditionService;
    }
    // Is currently returning a workcondition object with shifts which I dont want to have. Remove shifts from the return.
    @PostMapping("/addworkcondition")
    public ResponseEntity<Employment> addWorkCondition(@RequestBody Employment employment, @RequestParam Long personId) {
        return new ResponseEntity<>(workConditionService.addEmployment(employment, personId), HttpStatus.CREATED);
    }
}
