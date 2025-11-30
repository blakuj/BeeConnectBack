package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import lombok.*;

import java.util.List;

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
    private List<String> images;
    private Integer stock;
    private Boolean available;
    private String location;
    private Double weight;
    private String weightUnit;
}