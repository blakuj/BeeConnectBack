package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private String actionUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long relatedEntityId;
}