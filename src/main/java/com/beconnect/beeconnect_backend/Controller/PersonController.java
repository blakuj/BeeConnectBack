package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.UpdateProfileDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
//
@RestController
@RequestMapping("/api/profile")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<Person> getProfile() {
        System.out.println("\n=== CONTROLLER: GET /api/profile ===");

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println("Zalogowany użytkownik: " + email);

        Person person = personService.getProfile(email);

        System.out.println("Zwracam profil użytkownika");
        System.out.println("=== CONTROLLER: Koniec ===\n");

        return ResponseEntity.ok(person);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDTO dto) {
        System.out.println("\n=== CONTROLLER: PUT /api/profile ===");
        System.out.println("Otrzymane dane: " + dto);

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println("Zalogowany użytkownik: " + email);

        try {
            Person updatedPerson = personService.updateProfile(email, dto);

            System.out.println("Profil zaktualizowany!");
            System.out.println("=== CONTROLLER: Koniec ===\n");

            return ResponseEntity.ok(updatedPerson);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Błąd walidacji: " + e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            System.out.println("❌ Błąd: " + e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Wystąpił błąd serwera");

            return ResponseEntity.status(500).body(error);
        }
    }
}