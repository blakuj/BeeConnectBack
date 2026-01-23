package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50, message = "Typ dokumentu max 50 znaków")
    private String type;

    @NotBlank
    @Size(max = 255, message = "Ścieżka do pliku max 255 znaków")
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "verification_id")
    private BeeGardenVerification verification;
}