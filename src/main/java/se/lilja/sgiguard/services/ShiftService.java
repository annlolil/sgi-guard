package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ShiftDTO;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.WorkCondition;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.repositories.WorkConditionRepository;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftService implements ShiftServiceInterface {

    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;
    private final WorkConditionRepository workConditionRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository, PersonRepository personRepository, WorkConditionRepository workConditionRepository) {
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
        this.workConditionRepository = workConditionRepository;
    }

    @Override
    public Shift addShift(ShiftDTO shiftDTO, Long personId, Long workConditionId) {
        // Get the person that is logged in and connect it to the shift that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        WorkCondition workCondition = workConditionRepository.findById(workConditionId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Workcondition not found"));

        Shift shift = getShift(shiftDTO, person, workCondition);
        shiftRepository.save(shift);
        return shift;
    }

    private static Shift getShift(ShiftDTO shiftDTO, Person person, WorkCondition workCondition) {
        LocalDate startDate = shiftDTO.getStartDate();
        LocalDate endDate = shiftDTO.getEndDate();
        LocalTime startTime = shiftDTO.getStartTime();
        LocalTime endTime = shiftDTO.getEndTime();

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        Shift shift = new Shift();
        shift.setPerson(person);
        shift.setWorkCondition(workCondition);
        shift.setShiftStart(startDateTime);
        shift.setShiftEnd(endDateTime);
        return shift;
    }

    @Override
    public Shift updateShift() {
        return null;
    }

    @Override
    public Shift getShift() {
        return null;
    }

    @Override
    public List<Shift> getShifts(Long personId) {
        return shiftRepository.findShiftByPersonId(personId);
    }

    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public void deleteShift() {
    }
//
//    // A method that takes a shift and put localdate and localtime together
//    private LocalDateTime convertToLocalDateTime(LocalDate localDate, LocalTime localTime) {
//        return LocalDateTime.of(localDate, localTime);
//    }
}
