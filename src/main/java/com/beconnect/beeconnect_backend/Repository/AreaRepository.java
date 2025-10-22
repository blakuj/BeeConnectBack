package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByOwner(Person owner);

    // Policz obszary według statusu
    long countByAvailabilityStatus(String status);

    // Znajdź obszary według statusu
    List<Area> findByAvailabilityStatus(String status);
}