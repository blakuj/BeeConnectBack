package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BeeGardenVerification> verifications = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
    private List<Area> rentedAreas;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Area> ownedAreas;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> sellingProducts = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> purchaseHistory = new ArrayList<>();

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> salesHistory = new ArrayList<>();
}