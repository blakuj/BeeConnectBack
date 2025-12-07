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


    private Long otherUserId;
    private String otherUserFirstname;
    private String otherUserLastname;
    private String otherUserEmail;


    private Long productId;
    private String productName;
    private String productImage;

    private String lastMessageContent;
    private LocalDateTime lastMessageAt;

    private Integer unreadCount;

    private LocalDateTime createdAt;
}