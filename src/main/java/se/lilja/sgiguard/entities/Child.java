package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    LocalDate birthDate;

    @Column
    String firstName;

    @Column
    Boolean isSgiProtecting;

    @ManyToOne
    @JoinColumn(name = "person_id")
    Person person;

    public Child() {}
}
