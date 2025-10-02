package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String password;
}