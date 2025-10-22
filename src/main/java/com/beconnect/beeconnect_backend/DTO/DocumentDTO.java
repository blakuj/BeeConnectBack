package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

/**
 * DTO dla dokumentów weryfikacyjnych
 * Używane w VerificationResponseDTO
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDTO {
    private Long id;
    private String type;
    private String filePath;
    private String fileName; // Tylko nazwa pliku, bez pełnej ścieżki
}