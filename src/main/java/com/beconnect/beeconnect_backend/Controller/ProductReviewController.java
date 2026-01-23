package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.CreateProductReviewDTO;
import com.beconnect.beeconnect_backend.DTO.ProductReviewDTO;
import com.beconnect.beeconnect_backend.Service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews/products")
public class ProductReviewController {

    @Autowired
    private ProductReviewService reviewService;

    /**
     * POST /api/reviews/products
     * Utwórz opinię o produkcie
     */
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateProductReviewDTO dto) {
        try {
            ProductReviewDTO review = reviewService.createReview(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reviews/products/product/{productId}
     * Pobierz wszystkie opinie dla produktu
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        try {
            List<ProductReviewDTO> reviews = reviewService.getProductReviews(productId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reviews/products/my
     * Pobierz opinie wystawione przez zalogowanego użytkownika
     */
    @GetMapping("/my")
    public ResponseEntity<List<ProductReviewDTO>> getMyReviews() {
        try {
            List<ProductReviewDTO> reviews = reviewService.getMyReviews();
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/reviews/products/can-review/{orderId}
     * Sprawdź czy zamówienie może być ocenione
     */
    @GetMapping("/can-review/{orderId}")
    public ResponseEntity<Map<String, Boolean>> canReviewOrder(@PathVariable Long orderId) {
        boolean canReview = reviewService.canReviewOrder(orderId);
        return ResponseEntity.ok(Map.of("canReview", canReview));
    }
}