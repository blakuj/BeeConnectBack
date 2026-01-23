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
public class StartConversationDTO {
    @NotNull(message = "ID produktu jest wymagane")
    private Long productId;

    @NotBlank(message = "Wiadomość powitalna jest wymagana")
    @Size(max = 500, message = "Wiadomość jest zbyt długa")
    private String initialMessage;
}