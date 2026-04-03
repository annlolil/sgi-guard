package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.Child;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
}
