package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {
    // Statystyki użytkowników
    private long totalUsers;              // Wszyscy zarejestrowani
    private long verifiedBeekeepers;      // Zweryfikowani pszczelarze (BEEKEEPER)
    private long pendingVerifications;    // Oczekujące wnioski
    private long rejectedVerifications;   // Odrzucone wnioski

    // Statystyki obszarów
    private long totalAreas;              // Wszystkie obszary
    private long availableAreas;          // Dostępne obszary
    private long reservedAreas;           // Zarezerwowane obszary

    // Statystyki produktów (TODO - gdy dodamy moduł produktów)
    private long totalProducts;

    // Dodatkowe metryki
    private long adminsCount;             // Liczba adminów
}