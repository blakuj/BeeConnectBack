package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private LocalDateTime dateAdded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person owner;

    private String status;
}
