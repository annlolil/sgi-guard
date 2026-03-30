package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    LocalDateTime shiftStart;

    @Column
    LocalDateTime shiftEnd;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Shift() {}
}
