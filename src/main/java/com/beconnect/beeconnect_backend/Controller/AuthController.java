package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.LoginDTO;
import com.beconnect.beeconnect_backend.DTO.PersonDTO;
import com.beconnect.beeconnect_backend.DTO.RegisterDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Service.AuthService;
import com.beconnect.beeconnect_backend.Service.PersonService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PersonRepository personRepository;
    private final PersonService personService;

    public AuthController(AuthService authService, PersonRepository personRepository, PersonService personService) {
        this.authService = authService;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO request, HttpServletResponse response) {
        try {
            String token = authService.register(request);
            setJwtCookie(token, response);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO request, HttpServletResponse response) {
        try {
            System.out.println("Processing login request for: " + request.getLogin());
            String token = authService.login(request);
            System.out.println("Setting JWT cookie: " + token);
            setJwtCookie(token, response);
            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException e) {
            System.out.println("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/user")
    public ResponseEntity<Person> getCurrentUser(HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Fetching user for username: " + username);
        Person user = personRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    private void setJwtCookie(String token, HttpServletResponse response) {
        System.out.println("Adding JWT cookie: " + token);
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 24h
        // cookie.setSecure(true); // potem na produkcje
        response.addCookie(cookie);
    }
}