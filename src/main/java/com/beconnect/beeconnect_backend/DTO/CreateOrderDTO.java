package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderDTO {
    private Long productId;
    private Integer quantity;
    private String deliveryAddress;
    private String buyerNotes;
}