package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.Config.SessionStore;
import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.Service.AreaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {
    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<?> addArea(@RequestBody AreaDTO areaDto,
                                     @CookieValue(name = "session", required = false) String sessionToken) {
        if (sessionToken == null) {
            return ResponseEntity.status(401).body("Brak ciasteczka sesji");
        }

        String email = sessionStore.getEmail(sessionToken);
        if (email == null) {
            return ResponseEntity.status(401).body("Nieprawidłowa sesja");
        }

        areaService.addArea(areaDto, email);
        return ResponseEntity.ok("Area added successfully");
    }

    @GetMapping("/my")
    public ResponseEntity<List<AreaDTO>> getMyAreas(@CookieValue(name = "session", required = false) String sessionToken) {
        if (sessionToken == null) {
            return ResponseEntity.status(401).build();
        }

        String email = sessionStore.getEmail(sessionToken);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        List<AreaDTO> areas = areaService.getAreasForUser(email);
        return ResponseEntity.ok(areas);
    }

    @GetMapping
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        List<AreaDTO> areas = areaService.getAllAreas();
        return ResponseEntity.ok(areas);
    }
}

