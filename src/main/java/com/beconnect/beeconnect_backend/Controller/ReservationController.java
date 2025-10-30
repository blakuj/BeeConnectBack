package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.CreateReservationDTO;
import com.beconnect.beeconnect_backend.DTO.ReservationResponseDTO;
import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * POST /api/reservations
     * Utwórz i od razu potwierdź rezerwację (uproszczony proces)
     */
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody CreateReservationDTO dto) {
        try {
            ReservationResponseDTO reservation = reservationService.createReservation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/reservations/{id}/cancel
     * Anuluj rezerwację
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.getOrDefault("reason", "No reason provided");
            ReservationResponseDTO reservation = reservationService.cancelReservation(id, reason);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/my
     * Pobierz wszystkie moje rezerwacje (jako najemca)
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations() {
        try {
            List<ReservationResponseDTO> reservations = reservationService.getMyReservations();
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/reservations/my/status/{status}
     * Pobierz moje rezerwacje według statusu
     */
    @GetMapping("/my/status/{status}")
    public ResponseEntity<?> getMyReservationsByStatus(@PathVariable String status) {
        try {
            ReservationStatus statusEnum = ReservationStatus.valueOf(status.toUpperCase());
            List<ReservationResponseDTO> reservations = reservationService.getMyReservationsByStatus(statusEnum);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/reservations/areas
     * Pobierz rezerwacje dla moich obszarów (jako właściciel)
     */
    @GetMapping("/areas")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsForMyAreas() {
        try {
            List<ReservationResponseDTO> reservations = reservationService.getReservationsForMyAreas();
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/reservations/{id}
     * Pobierz szczegóły rezerwacji
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            ReservationResponseDTO reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}