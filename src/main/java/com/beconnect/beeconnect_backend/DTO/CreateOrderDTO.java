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
    @Max(value = 999, message = "Ilość może wynosić maksymalnie 999")
    private Integer quantity;

    @NotBlank(message = "Adres dostawy jest wymagany")
    private String deliveryAddress;

    private String buyerNotes;
}