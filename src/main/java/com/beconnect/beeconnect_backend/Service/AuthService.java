package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.LoginDTO;
import com.beconnect.beeconnect_backend.DTO.RegisterDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterDTO request) {
        if (personRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exitst");
        }

        Person user = Person.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .phone(request.getPhone())
                .email(request.getEmail())
                .login(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        personRepository.save(user);
        return "User registered successfully";
    }

    public boolean login(LoginDTO request) {
        return personRepository.findByLogin(request.getLogin())
                .map(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .orElse(false);
    }
}