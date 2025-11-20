package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // Reviewer info
    private Long reviewerId;
    private String reviewerFirstname;
    private String reviewerLastname;

    // Area info
    private Long areaId;
    private String areaName;

    // Reservation info
    private Long reservationId;
}