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
public class EditAreaDTO {
    private Long id;
    private String name;
    private List<String> images;

    private Set<FlowerDTO> flowers;

    private int maxHives;
    private float pricePerDay;
    private String description;
    private LocalDate endDate;
    private AvailabilityStatus availabilityStatus;
}