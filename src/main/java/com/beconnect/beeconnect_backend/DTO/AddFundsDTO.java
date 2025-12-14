package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddFundsDTO {
    @NotNull(message = "Kwota jest wymagana")
    @Min(value = 10, message = "Minimalna kwota doładowania to 10")
    Long amount;
}