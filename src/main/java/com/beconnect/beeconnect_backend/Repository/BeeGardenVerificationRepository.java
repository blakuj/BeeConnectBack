package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.BeeGardenVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BeeGardenVerificationRepository extends JpaRepository<BeeGardenVerification, Long> {

    // Znajdź wnioski po statusie
    List<BeeGardenVerification> findByStatus(Status status);

    // Policz wnioski według statusu
    long countByStatus(Status status);

    // Znajdź najnowsze wnioski
    @Query("SELECT v FROM BeeGardenVerification v ORDER BY v.creationDate DESC")
    List<BeeGardenVerification> findRecentVerifications();
}