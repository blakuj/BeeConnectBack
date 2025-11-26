package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.BadgeDTO;
import com.beconnect.beeconnect_backend.Service.BadgeService;
import com.beconnect.beeconnect_backend.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private PersonService personService;

    /**
     * GET /api/badges/my
     * Pobierz odznaki zalogowanego użytkownika
     */
    @GetMapping("/my")
    public ResponseEntity<List<BadgeDTO>> getMyBadges() {
        try {
            Long userId = personService.getProfile().getId();
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * GET /api/badges/user/{userId}
     * Pobierz odznaki konkretnego użytkownika
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BadgeDTO>> getUserBadges(@PathVariable Long userId) {
        try {
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    /**
     * POST /api/badges/check
     * Sprawdź i przyznaj odznaki zalogowanemu użytkownikowi
     */
    @PostMapping("/check")
    public ResponseEntity<List<BadgeDTO>> checkAndAwardBadges() {
        try {
            Long userId = personService.getProfile().getId();
            badgeService.checkAndAwardBadges(userId);
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * POST /api/badges/initialize
     * Inicjalizacja domyślnych odznak (tylko dla admina/developera)
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeBadges() {
        try {
            badgeService.initializeDefaultBadges();
            return ResponseEntity.ok("Default badges initialized");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}