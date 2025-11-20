package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductReviewDTO {
    private Long orderId;
    private Integer rating; // 1-5
    private String comment;
}