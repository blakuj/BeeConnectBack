package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.Role;
import lombok.*;
import java.math.BigDecimal; // Import

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonDTO {
    Long id;
    String firstname;
    String lastname;
    String email;
    String phone;
    Role role;
    BigDecimal balance; 
}