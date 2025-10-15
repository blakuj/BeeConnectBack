package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateProfileDTO {
    @NotBlank
    private String phone;
    @NotBlank
    private String email;
}