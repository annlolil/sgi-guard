package se.lilja.sgiguard.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
