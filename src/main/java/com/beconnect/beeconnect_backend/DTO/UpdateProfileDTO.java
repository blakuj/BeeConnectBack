package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateProfileDTO {
    @NotBlank(message = "Telefon jest wymagany")
    @Pattern(regexp = "\\d{9,11}", message = "Telefon musi składać się z 9 do 11 cyfr")
    private String phone;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format adresu email")
    private String email;
}