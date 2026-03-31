package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.WorkCondition;

@Repository
public interface WorkConditionRepository extends JpaRepository<WorkCondition, Long> {
}
