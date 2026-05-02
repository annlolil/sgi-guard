package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.repositories.ParentalLeaveRepository;

import java.time.LocalDate;

@Service
public class ParentalLeaveQueryService {

    private final ParentalLeaveRepository parentalLeaveRepository;

    @Autowired
    public ParentalLeaveQueryService(ParentalLeaveRepository parentalLeaveRepository) {
        this.parentalLeaveRepository = parentalLeaveRepository;
    }

    public double getSum(Long personId, LocalDate date) {
        Double sum = parentalLeaveRepository.sumExtentByPersonIdAndDate(personId, date);
        return sum != null ? sum : 0.0;
    }
}
