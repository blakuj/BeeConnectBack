// UserDTO.java
package com.beconnect.beeconnect_backend.DTO;

import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Enum.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;
    private boolean hasVerification;
    private Status verificationStatus;
    private String blockReason;
}
