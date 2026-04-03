package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.services.WorkConditionService;

@RestController
@RequestMapping("/workcondition")
public class WorkConditionController {

    private final WorkConditionService workConditionService;

    @Autowired
    public WorkConditionController(WorkConditionService workConditionService) {
        this.workConditionService = workConditionService;
    }
    // Is currently returning a workcondition object with shifts which I dont want to have. Remove shifts from the return.
    @PostMapping("/addworkcondition")
    public ResponseEntity<WorkCondition> addWorkCondition(@RequestBody WorkCondition workCondition, @RequestParam Long personId) {
        return new ResponseEntity<>(workConditionService.addWorkCondition(workCondition, personId), HttpStatus.CREATED);
    }
}
