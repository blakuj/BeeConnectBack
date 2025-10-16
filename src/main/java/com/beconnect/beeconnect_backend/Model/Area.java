package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @ElementCollection
    @CollectionTable(name = "area_coordinates", joinColumns = @JoinColumn(name = "area_id"))
    @Column(name = "coordinate")
    private List<String> coordinates = new ArrayList<>();

    @Column(nullable = false)
    private Double area;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_hives")
    private Integer maxHives;

    @Column(name = "price_per_day")
    private Double pricePerDay;

    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, OCCUPIED, PENDING, INACTIVE

    @Column(name = "date_added")
    private LocalDateTime dateAdded = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Person owner;

    // Opcjonalne - dla wynajmu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id")
    private Person renter;

    @Column(name = "rental_start_date")
    private LocalDateTime rentalStartDate;

    @Column(name = "rental_end_date")
    private LocalDateTime rentalEndDate;
}