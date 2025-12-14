package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowerDTO {
    private Long id;

    @NotBlank(message = "Nazwa kwiatu jest wymagana")
    private String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Kolor musi być w formacie HEX (np. #FF0000)")
    private String color;
}