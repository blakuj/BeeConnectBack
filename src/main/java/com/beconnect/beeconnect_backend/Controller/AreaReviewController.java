package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.AreaReviewDTO;
import com.beconnect.beeconnect_backend.DTO.CreateAreaReviewDTO;
import com.beconnect.beeconnect_backend.Service.AreaReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews/areas")
public class AreaReviewController {

    @Autowired
    private AreaReviewService reviewService;

    /**
     * POST /api/reviews/areas
     * Utwórz opinię o obszarze
     */
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateAreaReviewDTO dto) {
        try {
            AreaReviewDTO review = reviewService.createReview(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reviews/areas/area/{areaId}
     * Pobierz wszystkie opinie dla obszaru
     */
    @GetMapping("/area/{areaId}")
    public ResponseEntity<?> getAreaReviews(@PathVariable Long areaId) {
        try {
            List<AreaReviewDTO> reviews = reviewService.getAreaReviews(areaId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reviews/areas/my
     * Pobierz opinie wystawione przez zalogowanego użytkownika
     */
    @GetMapping("/my")
    public ResponseEntity<List<AreaReviewDTO>> getMyReviews() {
        try {
            List<AreaReviewDTO> reviews = reviewService.getMyReviews();
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/reviews/areas/can-review/{reservationId}
     * Sprawdź czy rezerwacja może być oceniona
     */
    @GetMapping("/can-review/{reservationId}")
    public ResponseEntity<Map<String, Boolean>> canReviewReservation(@PathVariable Long reservationId) {
        boolean canReview = reviewService.canReviewReservation(reservationId);
        return ResponseEntity.ok(Map.of("canReview", canReview));
    }
}