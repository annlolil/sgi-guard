package se.lilja.sgiguard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private Boolean isSgiProtecting;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonIgnoreProperties("children")
    private Person person;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getSgiProtecting() {
        return isSgiProtecting;
    }

    public void setSgiProtecting(Boolean sgiProtecting) {
        isSgiProtecting = sgiProtecting;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
