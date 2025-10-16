package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.BeeGardenVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeeGardenVerificationRepository extends JpaRepository<BeeGardenVerification, Long> {
    List<BeeGardenVerification> findByStatus(Status status);
    long countByStatus(Status status);
}