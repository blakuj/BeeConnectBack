package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // Reviewer info
    private Long reviewerId;
    private String reviewerFirstname;
    private String reviewerLastname;

    // Product info
    private Long productId;
    private String productName;

    // Order info
    private Long orderId;
}