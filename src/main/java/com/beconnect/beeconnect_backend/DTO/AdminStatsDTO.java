package com.beconnect.beeconnect_backend.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDTO {
    private Long totalUsers;
    private Long verifiedBeekeepers;
    private Long pendingVerifications;
    private Long totalAreas;
    private Long activeAreas;
    private Long rejectedVerifications;
    private Long totalVerifications;
}