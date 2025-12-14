package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Max(value = 50000000, message = "Powierzchnia obszaru jest zbyt duża (max 5000ha)")
    private double area;

    @Column(columnDefinition = "TEXT")
    @Size(max = 3000, message = "Opis obszaru nie może przekraczać 3000 znaków")
    private String description;

    @Min(1)
    @Max(value = 500, message = "Maksymalna liczba uli na obszarze to 500")
    private int maxHives;

    @PositiveOrZero
    @Max(value = 100000, message = "Cena za dzień jest zbyt wysoka")
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
    @Size(min = 3, max = 100, message = "Nazwa obszaru musi mieć od 3 do 100 znaków")
    private String name;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;
}