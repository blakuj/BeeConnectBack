package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.AdminStatsDTO;
import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.BeeGardenVerificationRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BeeGardenVerificationRepository verificationRepository;

    @Autowired
    private AreaRepository areaRepository;

    public AdminStatsDTO getAdminStatistics() {
        long totalUsers = personRepository.count();
        long verifiedBeekeepers = personRepository.countByRole(Role.BEEKEEPER);
        long pendingVerifications = verificationRepository.countByStatus(Status.PENDING);
        long rejectedVerifications = verificationRepository.countByStatus(Status.REJECTED);
        long totalVerifications = verificationRepository.count();
        long totalAreas = areaRepository.count();
        long activeAreas = areaRepository.countByStatus("AVAILABLE");

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .verifiedBeekeepers(verifiedBeekeepers)
                .pendingVerifications(pendingVerifications)
                .rejectedVerifications(rejectedVerifications)
                .totalVerifications(totalVerifications)
                .totalAreas(totalAreas)
                .activeAreas(activeAreas)
                .build();
    }
}