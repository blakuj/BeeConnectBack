package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateProductDTO {
    private String name;
    private String description;
    private Double price;
    private ProductCategory category;
    private String imageBase64;
    private Integer stock;
    private String location;
    private Double weight;
    private String weightUnit;
}