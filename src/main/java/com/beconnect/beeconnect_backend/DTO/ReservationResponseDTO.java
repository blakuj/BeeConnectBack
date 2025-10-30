package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Long id;
    private Long areaId;
    private String areaName;
    private String areaType;
    private Long tenantId;
    private String tenantFirstname;
    private String tenantLastname;
    private String tenantEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfHives;
    private Double totalPrice;
    private Double pricePerDay;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private String notes;
    private String cancellationReason;

    // Dodatkowe dane właściciela obszaru
    private String ownerFirstname;
    private String ownerLastname;
    private String ownerEmail;
    private String ownerPhone;
}