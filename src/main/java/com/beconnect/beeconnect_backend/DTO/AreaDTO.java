package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaDTO {
    private Long id;
    private String type;
    private List<List<Double>> coordinates;
    private Double area;
    private String description;
    private Integer maxHives;
    private Double pricePerDay;
    private String status;
    private String ownerName;
    private Long ownerId;
    private LocalDateTime dateAdded;

    // Konstruktor dla kompatybilności wstecznej
    public AreaDTO(String type, List<List<Double>> coordinates, Double area,
                   String description, Integer maxHives, Double pricePerDay,
                   String status, String ownerName) {
        this.type = type;
        this.coordinates = coordinates;
        this.area = area;
        this.description = description;
        this.maxHives = maxHives;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.ownerName = ownerName;
    }

    public AreaDTO(String type, List<List<Double>> coords, Double area, String description, Integer maxHives, Double pricePerDay, String status, String s, String s1) {
    }
}