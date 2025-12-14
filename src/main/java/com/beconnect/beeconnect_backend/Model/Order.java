package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    private Person buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private ProductReview review;

    @Column(nullable = false)
    @Min(1)
    @Max(value = 10000, message = "Maksymalna ilość produktów w jednym zamówieniu to 10000") // Upper bound
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    @Positive
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 19, scale = 2)
    @Positive
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private OrderStatus status = OrderStatus.COMPLETED;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    private LocalDateTime deliveredAt;

    @Size(max = 255, message = "Adres dostawy nie może przekraczać 255 znaków")
    private String deliveryAddress;

    @Size(max = 500, message = "Notatki do zamówienia nie mogą przekraczać 500 znaków")
    private String buyerNotes;

    @PrePersist
    protected void onCreate() {
        orderedAt = LocalDateTime.now();
    }
}