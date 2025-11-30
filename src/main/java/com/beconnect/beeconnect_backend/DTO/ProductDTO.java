package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private ProductCategory category;
    private List<String> images;
    private Integer stock;
    private Boolean available;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Dane sprzedawcy
    private Long sellerId;
    private String sellerFirstname;
    private String sellerLastname;
    private String sellerEmail;

    // Dodatkowe informacje
    private String location;
    private Double weight;
    private String weightUnit;
}