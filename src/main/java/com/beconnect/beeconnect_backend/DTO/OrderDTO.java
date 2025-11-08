package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderDTO {
    private Long id;

    // Informacje o kupującym
    private Long buyerId;
    private String buyerFirstname;
    private String buyerLastname;
    private String buyerEmail;

    // Informacje o sprzedawcy
    private Long sellerId;
    private String sellerFirstname;
    private String sellerLastname;
    private String sellerEmail;

    // Informacje o produkcie
    private Long productId;
    private String productName;
    private String productCategory;
    private String productImage;

    // Szczegóły zamówienia
    private Integer quantity;
    private Double pricePerUnit;
    private Double totalPrice;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private LocalDateTime deliveredAt;
    private String deliveryAddress;
    private String buyerNotes;
}