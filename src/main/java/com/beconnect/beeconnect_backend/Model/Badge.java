package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
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
    private String code; // np. "FREQUENT_SELLER", "TRUSTED_SELLER"

    @Column(nullable = false)
    private String name; // np. "Częste nowości"

    @Column(columnDefinition = "TEXT")
    private String description; // Opis jak zdobyć odznakę

    @Column(nullable = false)
    private String icon; // Font Awesome class, np. "fas fa-fire"

    @Column(nullable = false)
    private String color; // Kolor odznaki w hex, np. "#FFD700"

    // Relacja Many-to-Many z Person
    @ManyToMany(mappedBy = "badges")
    private Set<Person> persons = new HashSet<>();
}