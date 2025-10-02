package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDTO {
    private String type;
    private List<List<Double>> coordinates;
    private double area;
    private String description;
    private int maxHives;
    private double pricePerDay;
    private String status;
    private String ownerFirstName;
    private String ownerLastName;

}