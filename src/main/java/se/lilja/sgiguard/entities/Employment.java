package se.lilja.sgiguard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String workPlaceName;

    @Column(nullable = false)
    private Double currentEmploymentRate;

    @Column(nullable = false)
    private Double originalEmploymentRate;

    // Per week
    @Column(nullable = false)
    private Double originalWorkingHours;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @OneToMany(mappedBy = "employment")
    @JsonIgnore
    private List<Shift> shifts;
}
