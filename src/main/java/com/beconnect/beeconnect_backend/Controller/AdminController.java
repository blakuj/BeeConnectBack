package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.DashboardStatsDTO;
import com.beconnect.beeconnect_backend.DTO.VerificationDecisionDTO;
import com.beconnect.beeconnect_backend.DTO.VerificationResponseDTO;
import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * GET /api/admin/dashboard
     * Pobierz statystyki dla dashboardu
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * GET /api/admin/verifications
     * Pobierz wszystkie wnioski weryfikacyjne
     */
    @GetMapping("/verifications")
    public ResponseEntity<List<VerificationResponseDTO>> getAllVerifications() {
        try {
            List<VerificationResponseDTO> verifications = adminService.getAllVerifications();
            return ResponseEntity.ok(verifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * GET /api/admin/verifications/status/{status}
     * Pobierz wnioski według statusu (PENDING, APPROVED, REJECTED)
     */
    @GetMapping("/verifications/status/{status}")
    public ResponseEntity<List<VerificationResponseDTO>> getVerificationsByStatus(
            @PathVariable String status) {
        try {
            Status statusEnum = Status.valueOf(status.toUpperCase());
            List<VerificationResponseDTO> verifications = adminService.getVerificationsByStatus(statusEnum);
            return ResponseEntity.ok(verifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * GET /api/admin/verifications/{id}
     * Pobierz szczegóły konkretnego wniosku
     */
    @GetMapping("/verifications/{id}")
    public ResponseEntity<VerificationResponseDTO> getVerificationById(@PathVariable Long id) {
        try {
            VerificationResponseDTO verification = adminService.getVerificationById(id);
            return ResponseEntity.ok(verification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/admin/verifications/process
     * Rozpatrz wniosek (zatwierdź lub odrzuć)
     * Body: { verificationId, approved, comment }
     */
    @PostMapping("/verifications/process")
    public ResponseEntity<?> processVerification(@Valid @RequestBody VerificationDecisionDTO decision) {
        try {
            if (decision.getVerificationId() == null || decision.getApproved() == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            // Jeśli odrzucamy, wymagamy komentarza
            if (!decision.getApproved() && (decision.getComment() == null || decision.getComment().trim().isEmpty())) {
                return ResponseEntity.badRequest().body("Comment is required when rejecting");
            }

            adminService.processVerification(decision);
            return ResponseEntity.ok().body("Verification processed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}