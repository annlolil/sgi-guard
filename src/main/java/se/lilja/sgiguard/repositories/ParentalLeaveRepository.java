package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.entities.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParentalLeaveRepository extends JpaRepository<ParentalLeave, Long> {

    List<ParentalLeave> findByPersonIdAndDateBetween(Long personId, LocalDate from, LocalDate to);

    List<ParentalLeave> findByPersonIdAndDate(Long personId, LocalDate date);

    @Query("""
        SELECT COALESCE(SUM(p.extent), 0)
        FROM ParentalLeave p
        WHERE p.date = :date
        AND p.person.id = :personId
        """)
    double findExtentsByDateAndPersonId(
            @Param("date") LocalDate date,
            @Param("personId") Long personId);
}


