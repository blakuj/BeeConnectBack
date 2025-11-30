package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
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
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToMany(mappedBy = "flowers")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Area> areas = new HashSet<>();
}