package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String login;
    private String password;

    private float balance;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BeeGardenVerification verification;
}