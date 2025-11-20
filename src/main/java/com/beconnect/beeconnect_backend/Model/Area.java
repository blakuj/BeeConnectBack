package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private String type;

    @ElementCollection
    @CollectionTable(name = "area_coordinates", joinColumns = @JoinColumn(name = "area_id"))
    @Column(name = "coordinate")
    private List<String> coordinates;

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