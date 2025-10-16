package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // Nowe pola dla panelu admina
    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "block_reason")
    private String blockReason;

    // Relacje
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BeeGardenVerification verification;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BeeGarden beeGarden;

    // Metoda pomocnicza
    public String getFullName() {
        return firstname + " " + lastname;
    }
}
