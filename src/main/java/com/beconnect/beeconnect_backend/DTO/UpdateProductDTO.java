package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateProductDTO {
    @NotNull(message = "ID produktu jest wymagane")
    private Long id;

    @Size(min = 3, max = 100, message = "Nazwa produktu musi mieć od 3 do 100 znaków")
    private String name;

    @Size(min = 10, message = "Opis produktu musi mieć co najmniej 10 znaków")
    private String description;

    @Positive(message = "Cena musi być większa od 0")
    private Double price;

    private ProductCategory category;
    private List<String> images;

    @Min(value = 0, message = "Ilość nie może być ujemna")
    private Integer stock;

    private Boolean available;
    private String location;

    @Positive(message = "Waga musi być dodatnia")
    private Double weight;

    private String weightUnit;
}