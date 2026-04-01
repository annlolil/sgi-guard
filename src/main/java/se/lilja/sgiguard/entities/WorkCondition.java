package se.lilja.sgiguard.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WorkCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float currentEmploymentRate;

    @Column(nullable = false)
    private Float originalEmploymentRate;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonIgnoreProperties({"children", "shifts", "workConditions"})
    private Person person;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getCurrentEmploymentRate() {
        return currentEmploymentRate;
    }

    public void setCurrentEmploymentRate(Float currentEmploymentRate) {
        this.currentEmploymentRate = currentEmploymentRate;
    }

    public Float getOriginalEmploymentRate() {
        return originalEmploymentRate;
    }

    public void setOriginalEmploymentRate(Float originalEmploymentRate) {
        this.originalEmploymentRate = originalEmploymentRate;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
