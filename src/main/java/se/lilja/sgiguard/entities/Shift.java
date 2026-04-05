package se.lilja.sgiguard.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonIgnoreProperties("person")
    private WorkCondition workCondition;
}
