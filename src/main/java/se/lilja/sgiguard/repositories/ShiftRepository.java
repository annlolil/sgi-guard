package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.Shift;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("SELECT s FROM Shift s WHERE s.person.id = :personId " +
            "AND s.shiftStart < :rangeEnd " +
            "AND s.shiftEnd >= :rangeStart")
    List<Shift> findOverlappingShifts(
            @Param("personId") Long personId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd
    );

    List<Shift> findShiftByPersonId(Long personId);

}
