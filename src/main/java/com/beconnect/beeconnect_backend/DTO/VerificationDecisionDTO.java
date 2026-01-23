package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationDecisionDTO {
    @NotNull(message = "ID weryfikacji jest wymagane")
    private Long verificationId;

    @NotNull(message = "Decyzja (zatwierdź/odrzuć) jest wymagana")
    private Boolean approved;

    private String comment;
}