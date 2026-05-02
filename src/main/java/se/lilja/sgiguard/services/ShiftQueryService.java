package se.lilja.sgiguard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.Shift;
import se.lilja.sgiguard.repositories.ShiftRepository;
import se.lilja.sgiguard.utils.DateRange;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShiftQueryService {

    private final ShiftRepository shiftRepository;

    @Autowired
    public ShiftQueryService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    // Method that can list shifts a certain period of time
    // It also looks at shifts that can overlap a period by starting before the period but ending inside the period.
    public List<Shift> getShiftsForPersonInPeriod(Long personId, LocalDate from, LocalDate to) {

        DateRange range = DateRange.of(from, to);
        return shiftRepository.findOverlappingShifts(
                personId,
                range.start(),
                range.end()
        );
    }
}
