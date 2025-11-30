package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AreaDTO {
    private Long id;

    private Set<FlowerDTO> flowers;

    private List<List<Double>> coordinates;
    private double area;
    private String description;
    private int maxHives;
    private double pricePerDay;
    private AvailabilityStatus status;
    private String ownerFirstName;
    private String ownerLastName;
    private LocalDate availableFrom;
    private List<String> images;
    private String name;
    private Double averageRating;
    private Integer reviewCount;
    private Long reservationId;
}