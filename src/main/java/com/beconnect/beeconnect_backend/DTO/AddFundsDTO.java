package com.beconnect.beeconnect_backend.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddFundsDTO {
    Long amount;
}
