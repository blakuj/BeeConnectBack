package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private ProductCategory category;
    private String imageBase64;
    private Integer stock;
    private Boolean available;
    private String location;
    private Double weight;
    private String weightUnit;
}