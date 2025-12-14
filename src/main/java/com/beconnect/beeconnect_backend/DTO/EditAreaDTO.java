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
public class EditAreaDTO {
    @NotNull(message = "ID obszaru jest wymagane")
    private Long id;

    @NotBlank(message = "Nazwa obszaru jest wymagana")
    private String name;

    private List<String> images;

    private Set<FlowerDTO> flowers;

    @Min(value = 1, message = "Maksymalna liczba uli musi wynosić co najmniej 1")
    private int maxHives;

    @PositiveOrZero(message = "Cena nie może być ujemna")
    private float pricePerDay;

    private String description;

    @Future(message = "Data zakończenia musi być w przyszłości")
    private LocalDate endDate;

    @NotNull(message = "Status dostępności jest wymagany")
    private AvailabilityStatus availabilityStatus;
}