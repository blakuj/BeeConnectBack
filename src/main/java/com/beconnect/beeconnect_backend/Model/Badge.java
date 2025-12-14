package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "badge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(max = 50, message = "Kod odznaki max 50 znaków")
    private String code;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 100, message = "Nazwa odznaki max 100 znaków")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "Opis odznaki max 500 znaków")
    private String description;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50, message = "Nazwa ikony max 50 znaków")
    private String icon;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 10, message = "Kolor (HEX) max 10 znaków")
    private String color;

    @ManyToMany(mappedBy = "badges")
    private Set<Person> persons = new HashSet<>();
}