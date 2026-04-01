package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Implement AES-256 encryption for GDPR compliance
    @Column(unique = true, nullable = false)
    private String personalNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "person")
    private List<Child> children;

    // Enable registering more than one shift
    @OneToMany(mappedBy = "person")
    private List<Shift> shifts;

    // If a person have more than one employment
    @OneToMany(mappedBy = "person")
    private List<WorkCondition> workConditions;

//    public Person(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    public List<WorkCondition> getWorkConditions() {
        return workConditions;
    }

    public void setWorkConditions(List<WorkCondition> workConditions) {
        this.workConditions = workConditions;
    }
}
