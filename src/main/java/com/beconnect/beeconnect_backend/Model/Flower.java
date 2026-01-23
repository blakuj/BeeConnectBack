package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "flower")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(max = 50, message = "Nazwa kwiata max 50 znaków")
    private String name;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 4, max = 9, message = "Kolor musi być w formacie HEX (np. #FFFFFF)")
    private String color;

    @ManyToMany(mappedBy = "flowers")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Area> areas = new HashSet<>();
}