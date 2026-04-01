package se.lilja.sgiguard.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
// LomBok does not work, remove dependency or try to fix it.
@NoArgsConstructor //Empty constructor
@AllArgsConstructor // For testing
@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate shiftStartDate;

    @Column(nullable = false)
    private LocalTime shiftStartTime;

    @Column(nullable = false)
    private LocalDate shiftEndDate;

    @Column(nullable = false)
    private LocalTime shiftEndTime;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonIgnoreProperties({"shifts", "children", "workConditions"})
    private Person person;

    @ManyToOne
    @JoinColumn(name = "work_condition_id", nullable = false)
    @JsonIgnoreProperties({"person", "shifts", "children"})
    private WorkCondition workCondition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getShiftStartDate() {
        return shiftStartDate;
    }

    public void setShiftStartDate(LocalDate shiftStartDate) {
        this.shiftStartDate = shiftStartDate;
    }

    public LocalTime getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(LocalTime shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public LocalDate getShiftEndDate() {
        return shiftEndDate;
    }

    public void setShiftEndDate(LocalDate shiftEndDate) {
        this.shiftEndDate = shiftEndDate;
    }

    public LocalTime getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(LocalTime shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public WorkCondition getWorkCondition() {
        return workCondition;
    }

    public void setWorkCondition(WorkCondition workCondition) {
        this.workCondition = workCondition;
    }
}
