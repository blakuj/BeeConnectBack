package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "beeGarden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeeGarden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150, message = "Nazwa pasieki nie może przekraczać 150 znaków")
    private String name;

    @NotBlank
    @Size(max = 255, message = "Adres pasieki jest zbyt długi")
    private String adress;

    @Min(0)
    @Max(value = 10000, message = "Liczba uli jest nierealistyczna")
    private int hiveCount;

    @Size(max = 50, message = "Typ miodu max 50 znaków")
    private String honeyType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    @NotNull
    private Person person;
}