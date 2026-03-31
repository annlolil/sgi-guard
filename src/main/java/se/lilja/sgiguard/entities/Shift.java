package se.lilja.sgiguard.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    LocalDateTime shiftStart;

    @Column(nullable = false)
    LocalDateTime shiftEnd;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Shift() {}
}
