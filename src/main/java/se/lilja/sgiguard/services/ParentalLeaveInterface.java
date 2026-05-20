package se.lilja.sgiguard.services;


import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.dtos.ParentalLeaveResponse;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;

public interface ParentalLeaveInterface {

    ParentalLeaveResponse addParentalLeave(Person person, ParentalLeaveRequest request);

    String deleteParentalLeave(Long id);
}
