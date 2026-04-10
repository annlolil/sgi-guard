package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.Employment;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
}
