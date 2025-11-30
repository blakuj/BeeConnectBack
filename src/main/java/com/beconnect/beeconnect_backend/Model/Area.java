package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "area")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "area_flowers",
            joinColumns = @JoinColumn(name = "area_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Flower> flowers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "area_coordinates", joinColumns = @JoinColumn(name = "area_id"))
    @Column(name = "coordinate")
    private List<String> coordinates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private List<Image> images = new ArrayList<>();

    private double area;

    private String description;

    private int maxHives;

    private double pricePerDay;

    private LocalDate availableFrom;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Person owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Person tenant;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;

    @Lob
    private String imgBase64;

    private String name;

    // Rating fields
    private Double averageRating = 0.0;
    private Integer reviewCount = 0;

    // Relacja do opinii
    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AreaReview> reviews = new ArrayList<>();
}