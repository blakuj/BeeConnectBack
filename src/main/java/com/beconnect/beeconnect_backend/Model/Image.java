package com.beconnect.beeconnect_backend.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String fileContent;


}