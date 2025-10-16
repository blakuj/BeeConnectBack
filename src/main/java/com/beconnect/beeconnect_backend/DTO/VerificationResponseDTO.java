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
    private String userName;
    private String userEmail;
    private String userPhone;
    private String beeGardenName;
    private String address;
    private Integer hiveCount;
    private Integer yearsOfExperience;
    private String honeyType;
    private Status status;
    private LocalDateTime creationDate;
    private String comment;
    private List<DocumentDTO> documents;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DocumentDTO {
        private Long id;
        private String type;
        private String fileName;
        private String filePath;
    }
}