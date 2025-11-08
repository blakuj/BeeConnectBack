package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateMessageDTO {
    private Long conversationId;
    private String content;
}