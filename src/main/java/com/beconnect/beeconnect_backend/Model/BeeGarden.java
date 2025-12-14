package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank
    private String name;

    @NotBlank
    private String adress;

    @Min(0)
    private int hiveCount;

    private String honeyType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    @NotNull
    private Person person;
}