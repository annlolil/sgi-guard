package se.lilja.sgiguard.services;

import se.lilja.sgiguard.entities.Employment;

public interface EmploymentInterface {

    Employment addEmployment(Employment employment, Long personId);
}
