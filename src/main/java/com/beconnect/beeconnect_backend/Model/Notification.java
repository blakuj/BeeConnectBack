package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String message;

    @Column
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