package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EditAreaDTO {
    private Long id;
    private String name;
    private String imgBase64;
    private String type;
    private int maxHives;
    private float pricePerDay;
    private String description;
    private LocalDate endDate;
    private AvailabilityStatus availabilityStatus;
}
