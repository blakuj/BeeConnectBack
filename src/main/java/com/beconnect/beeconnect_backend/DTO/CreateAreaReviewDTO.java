package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAreaReviewDTO {
    @NotNull(message = "ID rezerwacji jest wymagane")
    private Long reservationId;

    @NotNull(message = "Ocena jest wymagana")
    @Min(value = 1, message = "Ocena musi wynosić co najmniej 1")
    @Max(value = 5, message = "Ocena nie może być wyższa niż 5")
    private Integer rating;

    @NotBlank(message = "Treść opinii nie może być pusta")
    @Size(min = 10, max = 1000, message = "Opinia musi mieć od 10 do 1000 znaków")
    private String comment;
}