package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationDTO {
    @NotBlank(message = "Nazwa pasieki jest wymagana")
    private String beeGardenName;

    @Min(value = 1, message = "Liczba uli musi wynosić co najmniej 1")
    private int countHives;

    @Min(value = 0, message = "Lata doświadczenia nie mogą być ujemne")
    private int yearsOfExperience;

    @NotBlank(message = "Adres pasieki jest wymagany")
    private String adress;

    private String honeyType;

    @NotBlank(message = "Typ dokumentu jest wymagany")
    private String docType;
}