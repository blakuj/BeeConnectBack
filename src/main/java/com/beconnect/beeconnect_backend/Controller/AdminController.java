package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.VerificationResponseDTO;
import com.beconnect.beeconnect_backend.DTO.AdminStatsDTO;
import com.beconnect.beeconnect_backend.Service.BeeGardenVerificationService;
import com.beconnect.beeconnect_backend.Service.AdminService;
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

    // Pobieranie wszystkich wniosków o weryfikację
    @GetMapping("/verifications")
    public ResponseEntity<List<VerificationResponseDTO>> getAllVerifications(
            @RequestParam(required = false) String status) {
        List<VerificationResponseDTO> verifications = verificationService.getAllVerifications(status);
        return ResponseEntity.ok(verifications);
    }

    // Pobieranie szczegółów wniosku
    @GetMapping("/verifications/{id}")
    public ResponseEntity<VerificationResponseDTO> getVerificationDetails(@PathVariable Long id) {
        VerificationResponseDTO verification = verificationService.getVerificationById(id);
        return ResponseEntity.ok(verification);
    }

    // Zatwierdzanie wniosku
    @PutMapping("/verifications/{id}/approve")
    public ResponseEntity<?> approveVerification(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String comment = body != null ? body.get("comment") : null;
        verificationService.approveVerification(id, comment);
        return ResponseEntity.ok(Map.of("message", "Wniosek został zatwierdzony"));
    }

    // Odrzucanie wniosku
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

    // Statystyki dla dashboardu
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getAdminStats() {
        AdminStatsDTO stats = adminService.getAdminStatistics();
        return ResponseEntity.ok(stats);
    }
}