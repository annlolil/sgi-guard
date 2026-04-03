package se.lilja.sgiguard.services;

import se.lilja.sgiguard.entities.WorkCondition;

public interface WorkConditionInterface {

    WorkCondition addWorkCondition(WorkCondition workCondition, Long personId);
}
