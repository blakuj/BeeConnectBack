package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import jakarta.validation.constraints.*;
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

    @NotEmpty(message = "Współrzędne są wymagane")
    @Size(min = 4, message = "Obszar musi składać się z co najmniej 4 punktów")
    private List<List<Double>> coordinates;

    @Positive(message = "Powierzchnia musi być większa od 0")
    private double area;

    private String description;

    @Min(value = 1, message = "Maksymalna liczba uli musi wynosić co najmniej 1")
    @Max(value = 999, message = "Ilość może wynosić maksymalnie 999")
    private int maxHives;

    @PositiveOrZero(message = "Cena za dzień nie może być ujemna")
    private double pricePerDay;

    private AvailabilityStatus status;
    private String ownerFirstName;
    private String ownerLastName;
    private LocalDate availableFrom;
    private List<String> images;

    @NotBlank(message = "Nazwa obszaru jest wymagana")
    private String name;

    private Double averageRating;
    private Integer reviewCount;
    private Long reservationId;
}