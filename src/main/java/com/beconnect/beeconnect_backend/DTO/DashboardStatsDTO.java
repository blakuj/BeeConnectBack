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

    // Statystyki produktów (marketplace)
    private long totalProducts;           // Wszystkie produkty
    private long totalOrders;             // Wszystkie zamówienia
    private long completedOrders;         // Zakończone zamówienia

    // Dodatkowe metryki
    private long adminsCount;             // Liczba adminów
}