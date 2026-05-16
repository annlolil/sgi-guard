package se.lilja.sgiguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lilja.sgiguard.entities.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsPersonByPersonalNumber(String personalNumber);

    Optional<Person> findPersonByPersonalNumber(String personalNumber);
}
