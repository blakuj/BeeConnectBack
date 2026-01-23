package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    @NotBlank(message = "Login/Email jest wymagany")
    private String login;

    @NotBlank(message = "Hasło jest wymagane")
    private String password;
}