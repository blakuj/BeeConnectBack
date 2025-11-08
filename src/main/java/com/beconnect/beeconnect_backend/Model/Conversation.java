package com.beconnect.beeconnect_backend.Model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant1_id", nullable = false)
    private Person participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant2_id", nullable = false)
    private Person participant2;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastMessageAt;

    @Column(columnDefinition = "TEXT")
    private String lastMessageContent;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
    }

    // Pomocnicza metoda do sprawdzania czy użytkownik jest uczestnikiem
    public boolean isParticipant(Person person) {
        return participant1.getId().equals(person.getId()) ||
                participant2.getId().equals(person.getId());
    }

    // Pomocnicza metoda do pobierania drugiego uczestnika
    public Person getOtherParticipant(Person currentUser) {
        return participant1.getId().equals(currentUser.getId()) ? participant2 : participant1;
    }
}