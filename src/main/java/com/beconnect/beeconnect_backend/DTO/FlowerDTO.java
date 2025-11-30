package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowerDTO {
    private Long id;
    private String name;
    private String color;
}