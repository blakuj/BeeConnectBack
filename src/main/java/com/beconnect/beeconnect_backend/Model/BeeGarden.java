package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "beeGarden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeeGarden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String adress;
    private int hiveCount;
    private String honeyType;


    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    private Person person;
}
