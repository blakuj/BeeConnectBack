package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private Person user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private NotificationType type;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100, message = "Tytuł powiadomienia max 100 znaków")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    @Size(max = 1000, message = "Treść powiadomienia max 1000 znaków")
    private String message;

    @Column
    @Size(max = 255, message = "Link akcji max 255 znaków")
    private String actionUrl;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private Long relatedEntityId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}