package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ShiftDTO;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.repositories.EmploymentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ShiftService implements ShiftServiceInterface {

    private final ShiftRepository shiftRepository;
    private final PersonRepository personRepository;
    private final EmploymentRepository employmentRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository,
                        PersonRepository personRepository,
                        EmploymentRepository employmentRepository) {
        this.shiftRepository = shiftRepository;
        this.personRepository = personRepository;
        this.employmentRepository = employmentRepository;
    }

    @Override
    public Shift addShift(ShiftDTO shiftDTO, Long personId, Long workConditionId) {
        // Get the person that is logged in and connect it to the shift that's being saved
        Person person = personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        Employment employment = employmentRepository.findById(workConditionId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Workcondition not found"));

        Shift shift = convertToEntity(shiftDTO, person, employment);
        return shiftRepository.save(shift);
    }

    private static Shift convertToEntity(ShiftDTO shiftDTO, Person person, Employment employment) {
        LocalDate startDate = shiftDTO.getStartDate();
        LocalDate endDate = shiftDTO.getEndDate();
        LocalTime startTime = shiftDTO.getStartTime();
        LocalTime endTime = shiftDTO.getEndTime();

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        Shift shift = new Shift();
        shift.setPerson(person);
        shift.setEmployment(employment);
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
}
