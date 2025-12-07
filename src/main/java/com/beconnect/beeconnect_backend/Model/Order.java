package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import jakarta.persistence.*;
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
    private Person buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private ProductReview review;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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

    public Person getSeller() {
        return product != null ? product.getSeller() : null;
    }
}