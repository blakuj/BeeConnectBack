package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {
    private long totalUsers;
    private long verifiedBeekeepers;
    private long pendingVerifications;
    private long rejectedVerifications;

    private long totalAreas;
    private long availableAreas;
    private long reservedAreas;

    private long totalProducts;
    private long totalOrders;
    private long completedOrders;

    private long adminsCount;
}