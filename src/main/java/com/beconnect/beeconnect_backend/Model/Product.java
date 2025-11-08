package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Lob
    private String imageBase64;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean available = true;

    private Double rating;

    private Integer reviewCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Relacja Many-to-One z Person (sprzedawca)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Person seller;

    // Opcjonalnie: lokalizacja produktu
    private String location;

    // Waga produktu (dla miodu w kg, dla innych w gramach)
    private Double weight;

    // Jednostka wagi
    private String weightUnit;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}