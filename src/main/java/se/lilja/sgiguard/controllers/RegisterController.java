package se.lilja.sgiguard.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.PersonRepository;

@Controller
public class RegisterController {

    private final PersonRepository repository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(PersonRepository repository,
                              PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("person", new Person());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Person person) {

        person.setPassword(
                passwordEncoder.encode(person.getPassword())
        );

        repository.save(person);

        return "redirect:/login";
    }
}
