package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StartConversationDTO {
    private Long productId;
    private String initialMessage;
}