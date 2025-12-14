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
    @Size(max = 150, message = "Nazwa pasieki max 150 znaków")
    private String beeGardenName;

    @Min(value = 1, message = "Liczba uli musi wynosić co najmniej 1")
    @Max(value = 10000, message = "Liczba uli nierealistyczna")
    private int countHives;

    @Min(value = 0, message = "Lata doświadczenia nie mogą być ujemne")
    @Max(value = 100, message = "Lata doświadczenia nierealistyczne")
    private int yearsOfExperience;

    @NotBlank(message = "Adres pasieki jest wymagany")
    @Size(max = 255, message = "Adres pasieki max 255 znaków")
    private String adress;

    @Size(max = 50, message = "Typ miodu max 50 znaków")
    private String honeyType;

    @NotBlank(message = "Typ dokumentu jest wymagany")
    @Size(max = 50, message = "Typ dokumentu max 50 znaków")
    private String docType;
}