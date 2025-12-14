package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@SoftDelete
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100, message = "Nazwa produktu nie może przekraczać 100 znaków")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Opis produktu nie może przekraczać 2000 znaków")
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @Positive
    @Max(value = 1000000, message = "Cena produktu przekracza dopuszczalny limit")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ProductCategory category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Conversation> conversations = new ArrayList<>();

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(value = 1000, message = "Stan magazynowy nie może przekraczać 1000 sztuk")
    private Integer stock;

    @Column(nullable = false)
    private Boolean available = true;

    private Double rating = 0.0;
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    private Person seller;

    @Size(max = 255, message = "Lokalizacja zbyt długa")
    private String location;

    @Positive
    @Max(value = 100000, message = "Waga jest zbyt duża")
    private Double weight;

    @Size(max = 10, message = "Jednostka wagi max 10 znaków")
    private String weightUnit;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rating == null) rating = 0.0;
        if (reviewCount == null) reviewCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}