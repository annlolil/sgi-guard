package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.entities.Child;
import se.lilja.sgiguard.services.ChildService;

@RestController
@RequestMapping("/child")
public class ChildController {

    private final ChildService childService;

    @Autowired
    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @PostMapping("/addchild")
    public ResponseEntity<Child> addChild(@RequestBody Child child, @RequestParam Long personId) {
        return new ResponseEntity<>(childService.addChild(child, personId), HttpStatus.CREATED);
    }
}
