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
    @Size(min = 10, max = 2000, message = "Opis produktu musi mieć od 10 do 2000 znaków") // Dodano max
    private String description;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być większa od 0")
    @Max(value = 1000000, message = "Cena przekracza limit") // Dodano
    private Double price;

    @NotNull(message = "Kategoria jest wymagana")
    private ProductCategory category;

    private List<String> images;

    @NotNull(message = "Ilość magazynowa jest wymagana")
    @Min(value = 0, message = "Ilość nie może być ujemna")
    @Max(value = 10000, message = "Maksymalna ilość to 10000")
    private Integer stock;

    @Size(max = 255, message = "Lokalizacja zbyt długa")
    private String location;

    @Positive(message = "Waga musi być dodatnia")
    @Max(value = 100000, message = "Waga zbyt duża")
    private Double weight;

    @Size(max = 10, message = "Jednostka wagi max 10 znaków")
    private String weightUnit;
}