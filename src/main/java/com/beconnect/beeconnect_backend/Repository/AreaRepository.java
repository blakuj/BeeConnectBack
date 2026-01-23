package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {
    long countByAvailabilityStatus(AvailabilityStatus availabilityStatus);

    @Query(value = "SELECT * FROM area WHERE id IN :ids", nativeQuery = true)
    List<Area> findAllByIdNative(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM area WHERE owner_id = :ownerId", nativeQuery = true)
    List<Area> findByOwnerNative(@Param("ownerId") Long ownerId);

}