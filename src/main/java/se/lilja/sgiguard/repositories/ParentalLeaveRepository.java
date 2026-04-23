package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParentalLeaveRepository extends JpaRepository<ParentalLeave, Long> {

    List<ParentalLeave> findByPersonIdAndDateBetween(Long personId, LocalDate from, LocalDate to);
}
