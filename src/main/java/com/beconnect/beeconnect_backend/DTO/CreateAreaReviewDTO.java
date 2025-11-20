package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAreaReviewDTO {
    private Long reservationId;
    private Integer rating; // 1-5
    private String comment;
}