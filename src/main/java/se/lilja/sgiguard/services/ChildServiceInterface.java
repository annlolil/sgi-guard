package se.lilja.sgiguard.services;

import se.lilja.sgiguard.entities.Child;

public interface ChildServiceInterface {

    Child addChild(Child chiLd, Long personId);
    Child updateChild(Child child);
    void deleteChild(Child child);

}
