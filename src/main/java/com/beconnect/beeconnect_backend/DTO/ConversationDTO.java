package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConversationDTO {
    private Long id;

    // Dane drugiego uczestnika (nie zalogowanego użytkownika)
    private Long otherUserId;
    private String otherUserFirstname;
    private String otherUserLastname;
    private String otherUserEmail;

    // Ostatnia wiadomość
    private String lastMessageContent;
    private LocalDateTime lastMessageAt;

    // Liczba nieprzeczytanych wiadomości
    private Integer unreadCount;

    private LocalDateTime createdAt;
}