package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import lombok.*;

import java.time.LocalDateTime;

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
    private String imageBase64;
    private Integer stock;
    private Boolean available;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long sellerId;
    private String sellerFirstname;
    private String sellerLastname;
    private String sellerEmail;

    private String location;
    private Double weight;
    private String weightUnit;
}