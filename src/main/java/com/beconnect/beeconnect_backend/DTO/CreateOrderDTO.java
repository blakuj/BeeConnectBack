package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderDTO {
    @NotNull(message = "ID produktu jest wymagane")
    private Long productId;

    @NotNull(message = "Ilość jest wymagana")
    @Min(value = 1, message = "Ilość musi wynosić co najmniej 1")
    @Max(value = 10000, message = "Ilość może wynosić maksymalnie 10000")
    private Integer quantity;

    @NotBlank(message = "Adres dostawy jest wymagany")
    @Size(max = 255, message = "Adres dostawy zbyt długi (max 255)")
    private String deliveryAddress;

    @Size(max = 500, message = "Notatki zbyt długie (max 500)")
    private String buyerNotes;
}