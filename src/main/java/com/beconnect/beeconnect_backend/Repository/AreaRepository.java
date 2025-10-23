package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByOwner(Person owner);


    long countByAvailabilityStatus(AvailabilityStatus availabilityStatus);

    List<Area> findByAvailabilityStatus(AvailabilityStatus availabilityStatus);
}