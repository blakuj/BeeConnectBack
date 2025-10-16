package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bee_garden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeeGarden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String adress;

    private Integer hiveCount;

    private String honeyType;

    @OneToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}