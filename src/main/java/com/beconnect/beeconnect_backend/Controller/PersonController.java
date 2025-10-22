package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.AddFundsDTO;
import com.beconnect.beeconnect_backend.DTO.ChangePasswordDTO;
import com.beconnect.beeconnect_backend.DTO.UpdateProfileDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Service.PersonService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<Person> getProfile() {
        return ResponseEntity.ok(personService.getProfile());
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDTO dto) {
        try {
            personService.updateProfile(dto);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto) {
        try {
            personService.changePassword(dto);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}