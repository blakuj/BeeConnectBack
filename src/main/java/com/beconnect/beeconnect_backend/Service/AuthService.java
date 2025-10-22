package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.LoginDTO;
import com.beconnect.beeconnect_backend.DTO.RegisterDTO;
import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonRepository personRepository;

    public String register(RegisterDTO request) {
        if (personRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        Person user = Person.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .phone(request.getPhone())
                .email(request.getEmail())
                .login(request.getEmail())
                .balance(0.0f)
                .role(Role.USER)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        personRepository.save(user);
        return generateJwtToken(user);
    }

    public String login(LoginDTO request) {
        Person user = personRepository
                .findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateJwtToken(user);
    }

    private String generateJwtToken(Person user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().toString())
                .claim("firstname", user.getFirstname())
                .claim("lastname", user.getLastname())
                .claim("phone", user.getPhone())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}