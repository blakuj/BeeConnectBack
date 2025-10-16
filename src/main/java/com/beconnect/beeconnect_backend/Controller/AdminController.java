// Rozszerzenie AdminController.java o brakujące endpointy

package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.VerificationResponseDTO;
import com.beconnect.beeconnect_backend.DTO.AdminStatsDTO;
import com.beconnect.beeconnect_backend.DTO.UserDTO;
import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.Service.BeeGardenVerificationService;
import com.beconnect.beeconnect_backend.Service.AdminService;
import com.beconnect.beeconnect_backend.Service.PersonService;
import com.beconnect.beeconnect_backend.Service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private BeeGardenVerificationService verificationService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private PersonService personService;

    @Autowired
    private AreaService areaService;

    // ============ ISTNIEJĄCE ENDPOINTY ============

    @GetMapping("/verifications")
    public ResponseEntity<List<VerificationResponseDTO>> getAllVerifications(
            @RequestParam(required = false) String status) {
        List<VerificationResponseDTO> verifications = verificationService.getAllVerifications(status);
        return ResponseEntity.ok(verifications);
    }

    @GetMapping("/verifications/{id}")
    public ResponseEntity<VerificationResponseDTO> getVerificationDetails(@PathVariable Long id) {
        VerificationResponseDTO verification = verificationService.getVerificationById(id);
        return ResponseEntity.ok(verification);
    }

    @PutMapping("/verifications/{id}/approve")
    public ResponseEntity<?> approveVerification(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        verificationService.approveVerification(id, comment);
        return ResponseEntity.ok(Map.of("message", "Wniosek został zatwierdzony"));
    }

    @PutMapping("/verifications/{id}/reject")
    public ResponseEntity<?> rejectVerification(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Powód odrzucenia jest wymagany"));
        }
        verificationService.rejectVerification(id, reason);
        return ResponseEntity.ok(Map.of("message", "Wniosek został odrzucony"));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getAdminStats() {
        AdminStatsDTO stats = adminService.getAdminStatistics();
        System.out.println(stats);
        return ResponseEntity.ok(stats);
    }

    // ============ NOWE ENDPOINTY - UŻYTKOWNICY ============

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {
        List<UserDTO> users = adminService.getAllUsers(search, role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Long id) {
        UserDTO user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        adminService.blockUser(id, reason);
        return ResponseEntity.ok(Map.of("message", "Użytkownik został zablokowany"));
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
        return ResponseEntity.ok(Map.of("message", "Użytkownik został odblokowany"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        String newRole = body.get("role");
        adminService.changeUserRole(id, newRole);
        return ResponseEntity.ok(Map.of("message", "Rola użytkownika została zmieniona"));
    }

    // ============ NOWE ENDPOINTY - OBSZARY ============

    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas(
            @RequestParam(required = false) String status) {
        List<AreaDTO> areas = adminService.getAllAreas(status);
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/areas/{id}")
    public ResponseEntity<AreaDTO> getAreaDetails(@PathVariable Long id) {
        AreaDTO area = adminService.getAreaById(id);
        return ResponseEntity.ok(area);
    }

    @DeleteMapping("/areas/{id}")
    public ResponseEntity<?> deleteArea(@PathVariable Long id,
                                        @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "Usunięte przez administratora";
        boolean notifyOwner = body != null && Boolean.parseBoolean(body.get("notifyOwner"));

        adminService.deleteArea(id, reason, notifyOwner);
        return ResponseEntity.ok(Map.of("message", "Obszar został usunięty"));
    }

    @PutMapping("/areas/{id}/status")
    public ResponseEntity<?> changeAreaStatus(@PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        adminService.changeAreaStatus(id, newStatus);
        return ResponseEntity.ok(Map.of("message", "Status obszaru został zmieniony"));
    }
}