package com.beconnect.beeconnect_backend.Model;

import com.beconnect.beeconnect_backend.Enum.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeeGardenVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private Status status = Status.PENDING;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "reviewed_date")
    private LocalDateTime reviewedDate;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @NotNull
    private Person person;

    @OneToMany(mappedBy = "verification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();
}