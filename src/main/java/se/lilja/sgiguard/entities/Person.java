package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Implement AES-256 encryption for GDPR compliance
    @Column(unique = true, nullable = false)
    String personalNumber;

    @Column
    String firstName;

    @Column
    String lastName;

    @OneToMany(mappedBy = "person")
    private List<Child> children;

    // Enable registering more than one shift
    @OneToMany(mappedBy = "person")
    private List<Shift> shifts;

    // If a person have more than one employment
    @OneToMany(mappedBy = "person")
    private List<WorkCondition> workConditions;

    public Person(){}
}
