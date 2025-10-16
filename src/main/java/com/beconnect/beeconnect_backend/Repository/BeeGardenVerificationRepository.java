package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.BeeGardenVerification;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeeGardenVerificationRepository extends JpaRepository<BeeGardenVerification, Long> {
    List<BeeGardenVerification> findByStatus(Status status);
    long countByStatus(Status status);
    Optional<BeeGardenVerification> findByPerson(Person person);

    @Query("SELECT v FROM BeeGardenVerification v ORDER BY v.creationDate DESC")
    List<BeeGardenVerification> findAllOrderByCreationDateDesc();

    @Query("SELECT v FROM BeeGardenVerification v WHERE v.status = :status ORDER BY v.creationDate DESC")
    List<BeeGardenVerification> findByStatusOrderByCreationDateDesc(Status status);
}