package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationDTO {
    private String beeGardenName;
    private int countHives;
    private int yearsOfExperience;
    private String adress;
    private String honeyType;
    private String docType;
}
