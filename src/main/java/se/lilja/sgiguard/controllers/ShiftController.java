package se.lilja.sgiguard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;
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

    @PostMapping("/addshift")
    public Shift addShift(@RequestBody Shift shift) {
        return shiftService.addShift(shift);
    }

    @GetMapping("/getshifts")
    public List<Shift> getShifts(Person person) {
        return shiftService.getShifts(person);
    }

    @GetMapping("/getallshifts")
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }
}
