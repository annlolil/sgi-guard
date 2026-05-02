package se.lilja.sgiguard.services;


import se.lilja.sgiguard.dtos.ParentalLeaveRequest;
import se.lilja.sgiguard.dtos.ParentalLeaveResponse;
import se.lilja.sgiguard.entities.ParentalLeave;

public interface ParentalLeaveInterface {

    ParentalLeaveResponse addParentalLeave(ParentalLeaveRequest request);

    String deleteParentalLeave(Long id);
}
