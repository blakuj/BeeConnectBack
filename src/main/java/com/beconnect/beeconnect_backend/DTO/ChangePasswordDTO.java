package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChangePasswordDTO {
    @NotBlank(message = "Stare hasło jest wymagane")
    private String oldPassword;

    @NotBlank(message = "Nowe hasło jest wymagane")
    @Size(min = 8, message = "Nowe hasło musi mieć co najmniej 8 znaków")
    private String newPassword;

    @NotBlank(message = "Potwierdzenie hasła jest wymagane")
    private String confirmPassword;
}