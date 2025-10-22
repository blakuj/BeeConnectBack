package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResponseDTO {
    private Long id;
    private Status status;
    private String comment;
    private LocalDateTime creationDate;
    private LocalDateTime reviewedDate;
    private String reviewedBy;

    // Dane użytkownika
    private Long personId;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    // Dane pasieki
    private String beeGardenName;
    private String beeGardenAddress;
    private Integer hiveCount;
    private String honeyType;

    private List<DocumentDTO> documents;
}