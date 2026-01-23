package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateMessageDTO {
    @NotNull(message = "ID konwersacji jest wymagane")
    private Long conversationId;

    @NotBlank(message = "Treść wiadomości nie może być pusta")
    @Size(max = 500, message = "Wiadomość jest zbyt długa (max 500 znaków)")
    private String content;
}