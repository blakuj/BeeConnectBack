package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.LoginDTO;
import com.beconnect.beeconnect_backend.DTO.RegisterDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
    }



    @PostMapping("/login")
    public boolean login(@RequestBody LoginDTO request, HttpServletResponse response) {
        boolean success = authService.login(request);

        if (success) {
            String sessionToken = UUID.randomUUID().toString();
            String email = request.getLogin();

            sessionStore.storeSession(sessionToken, email);

            Cookie cookie = new Cookie("session", sessionToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);
        }

        return success;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "session", required = false) String sessionToken) {
        if (sessionToken == null) return ResponseEntity.status(401).body("Brak ciasteczka sesji");

        String email = sessionStore.getEmail(sessionToken);
        if (email == null) return ResponseEntity.status(401).body("Nieprawidłowa sesja");

        return personRepository.findByEmail(email)
                .map(user -> Map.of(
                        "firstname", user.getFirstname(),
                        "lastname", user.getLastname(),
                        "email", user.getEmail(),
                        "phone", user.getPhone()
                ))
                .map(ResponseEntity::ok)
                .orElse(null);

    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "session", required = false) String sessionToken,
                                    HttpServletResponse response) {
        if (sessionToken != null) {
            sessionStore.removeSession(sessionToken);

            Cookie cookie = new Cookie("session", "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return ResponseEntity.ok("Wylogowano");
    }


}