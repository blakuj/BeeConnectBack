package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationDTO {
    private Long areaId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfHives;
    private String notes;
}