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
public class CreateProductDTO {
    @NotBlank(message = "Nazwa produktu jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa produktu musi mieć od 3 do 100 znaków")
    private String name;

    @NotBlank(message = "Opis produktu jest wymagany")
    @Size(min = 10, message = "Opis produktu musi mieć co najmniej 10 znaków")
    private String description;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być większa od 0")
    private Double price;

    @NotNull(message = "Kategoria jest wymagana")
    private ProductCategory category;

    private List<String> images;

    @NotNull(message = "Ilość magazynowa jest wymagana")
    @Min(value = 0, message = "Ilość nie może być ujemna")
    private Integer stock;

    private String location;

    @Positive(message = "Waga musi być dodatnia")
    private Double weight;

    private String weightUnit;
}