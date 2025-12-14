package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    private String deliveryAddress;

    private String buyerNotes;

    @PrePersist
    protected void onCreate() {
        orderedAt = LocalDateTime.now();
    }

}