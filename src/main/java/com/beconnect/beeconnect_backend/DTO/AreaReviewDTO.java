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

    private Long reviewerId;
    private String reviewerFirstname;
    private String reviewerLastname;

    private Long areaId;
    private String areaName;

    private Long reservationId;
}