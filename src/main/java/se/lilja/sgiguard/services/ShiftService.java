package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.lilja.sgiguard.dtos.ShiftRequest;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.entities.Employment;
import se.lilja.sgiguard.models.ShiftType;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.repositories.EmploymentRepository;
import se.lilja.sgiguard.utils.DateRange;

import java.time.LocalDateTime;
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
    public Shift addShift(ShiftRequest shiftRequest, Long personId) {
        // Get the person that is logged in and connect it to the shift that's being saved
        Person person = personRepository.findById(personId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        Employment employment = employmentRepository.findById(shiftRequest.getEmploymentId()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment not found"));

        // Fetch start and end time for the shift and validate that
        // shifts start date and time is before end date and time
        // shift is not more than 24 hours long
        LocalDateTime startDateTime = LocalDateTime.of(shiftRequest.getStartDate(), shiftRequest.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.of(shiftRequest.getEndDate(), shiftRequest.getEndTime());
        DateRange dateRange = new DateRange(startDateTime, endDateTime);
        dateRange.validateAsShift();

        int breakMinutes = shiftRequest.getBreakMinutes();
        ShiftType type = shiftRequest.getType();

        Shift shift = convertToEntity(startDateTime, endDateTime, person, employment, breakMinutes, type);
        return shiftRepository.save(shift);
    }

    private static Shift convertToEntity(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Person person,
            Employment employment,
            Integer breakMinutes,
            ShiftType type) {

        Shift shift = new Shift();
        shift.setPerson(person);
        shift.setEmployment(employment);
        shift.setShiftStart(startDateTime);
        shift.setShiftEnd(endDateTime);
        shift.setBreakMinutes(60);
        shift.setType(type);
        return shift;
    }

    @Override
    public List<Shift> getShifts(Long personId) {

        personRepository.findById(personId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        return shiftRepository.findShiftByPersonId(personId);
    }

    @Override
    public String deleteShift(Long shiftId) {

        shiftRepository.findById(shiftId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));

        shiftRepository.deleteById(shiftId);
        return "Shift deleted";
    }

//    private void validateShiftDuration(LocalDateTime start, LocalDateTime end) {
//        Duration duration = Duration.between(start, end);
//
//        if (duration.isNegative()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skiftet kan inte sluta innan det börjar.");
//        }
//
//        if (duration.toHours() > 24) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "Ett skift kan inte vara längre än 24 timmar. Kontrollera datum och tid.");
//        }
//    }
}
