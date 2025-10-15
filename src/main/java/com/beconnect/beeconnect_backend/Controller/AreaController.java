package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.Config.SessionStore;
import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.Service.AreaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AreaController {
    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private AreaService areaService;

    @PostMapping("/addArea")
    public ResponseEntity<?> addArea(@RequestBody AreaDTO areaDto) {
        try {
            areaService.addArea(areaDto);
            return ResponseEntity.ok("Area added successfully");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

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

    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        List<AreaDTO> areas = areaService.getAllAreas();
        return ResponseEntity.ok(areas);
    }
}

