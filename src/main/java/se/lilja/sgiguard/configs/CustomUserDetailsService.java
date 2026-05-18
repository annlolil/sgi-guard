package se.lilja.sgiguard.configs;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.PersonRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final PersonRepository repository;

    public CustomUserDetailsService(
            PersonRepository repository) {

        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String personalNumber)
            throws UsernameNotFoundException {

        Person person = repository
                .findPersonByPersonalNumber(personalNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"));

        return User.builder()
                .username(person.getPersonalNumber())
                .password(person.getPassword())
                .roles("USER")
                .build();
    }
}
