package se.lilja.sgiguard.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.lilja.sgiguard.dtos.RegisterRequest;
import se.lilja.sgiguard.entities.Person;
import se.lilja.sgiguard.repositories.PersonRepository;
import se.lilja.sgiguard.services.PersonService;

@Controller
public class RegisterController {

    private final PersonRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterController(PersonRepository repository,
                              PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute RegisterRequest registerRequest,
            BindingResult bindingResult,
            Model model) {

        if(bindingResult.hasErrors()) {

            model.addAttribute(
                    "registerError",
                    bindingResult.getFieldError()
                            .getDefaultMessage());

            return "register";
        }

        try {

            Person person = new Person();

            person.setPersonalNumber(
                    registerRequest.getPersonalNumber());

            person.setFirstName(
                    registerRequest.getFirstName());

            person.setLastName(
                    registerRequest.getLastName());

            person.setPassword(
                    passwordEncoder.encode(
                            registerRequest.getPassword()));

            repository.save(person);

            return "redirect:/login";

        }
        catch(Exception e) {

            model.addAttribute(
                    "registerError",
                    e.getMessage());

            return "register";
        }
    }
}
