package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MessageDTO {
    private Long id;
    private Long conversationId;

    private Long senderId;
    private String senderFirstname;
    private String senderLastname;

    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;

    private Boolean isMine;
}