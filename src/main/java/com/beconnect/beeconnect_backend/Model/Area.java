package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "area")
@SQLDelete(sql = "UPDATE area SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
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

    private double area;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int maxHives;
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

    private String name;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private boolean deleted = false;
}