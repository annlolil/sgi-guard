package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class WorkCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    Float currentEmploymentRate;

    @Column
    Float originalEmploymentRate;

    @Column
    LocalDate validFrom;

    @Column
    LocalDate validTo;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public WorkCondition() {}
}
