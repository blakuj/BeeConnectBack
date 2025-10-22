package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationDecisionDTO {
    private Long verificationId;
    private Boolean approved; // true = zatwierdź, false = odrzuć
    private String comment; // Komentarz admina
}