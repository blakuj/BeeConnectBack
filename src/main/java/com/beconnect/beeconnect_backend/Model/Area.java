package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "area")
@SoftDelete
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "geometry")
    private Polygon polygon;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "area_flowers",
            joinColumns = @JoinColumn(name = "area_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Flower> flowers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private List<Image> images = new ArrayList<>();

    @Positive
    private double area;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(1)
    private int maxHives;

    @PositiveOrZero
    private double pricePerDay;

    private LocalDate availableFrom;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Person owner;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;

    @Lob
    private String imgBase64;

    @NotBlank
    private String name;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;
}