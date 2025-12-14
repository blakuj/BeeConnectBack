package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String name;

    @Column(nullable = false)
    @NotBlank
    private String color;

    @ManyToMany(mappedBy = "flowers")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Area> areas = new HashSet<>();
}