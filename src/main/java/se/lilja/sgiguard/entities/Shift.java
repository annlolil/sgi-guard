package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.*;
import se.lilja.sgiguard.models.ShiftType;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Shift beginning", nullable = false)
    private LocalDateTime shiftStart;

    @Column(nullable = false)
    private LocalDateTime shiftEnd;

    @Column
    private Integer breakMinutes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShiftType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", shiftStart=" + shiftStart +
                ", shiftEnd=" + shiftEnd +
                ", breakMinutes=" + breakMinutes +
                ", type=" + type +
                ", person=" + person +
                '}';
    }
}
