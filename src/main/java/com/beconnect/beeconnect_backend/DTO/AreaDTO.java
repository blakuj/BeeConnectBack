package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AreaDTO {
    private Long id;
    private String type;
    private List<List<Double>> coordinates;
    private double area;
    private String description;
    private int maxHives;
    private double pricePerDay;
    private AvailabilityStatus status;
    private String ownerFirstName;
    private String ownerLastName;

    private LocalDate availableFrom ;
    private String imgBase64;

    private String name;
}