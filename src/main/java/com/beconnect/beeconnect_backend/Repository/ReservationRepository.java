package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "SELECT area_id FROM reservation WHERE tenant_id = :tenantId", nativeQuery = true)
    List<Long> findAreaIdsByTenant(@Param("tenantId") Long tenantId);

    @Query(value = "SELECT * FROM reservation WHERE tenant_id = :tenantId", nativeQuery = true)
    List<Reservation> findByTenantNative(@Param("tenantId") Long tenantId);

    @Query(value = "SELECT * FROM reservation WHERE area_id IN :areaIds", nativeQuery = true)
    List<Reservation> findByAreaIdInNative(@Param("areaIds") List<Long> areaIds);

    List<Reservation> findByTenantAndStatus(Person tenant, ReservationStatus status);

    List<Reservation> findByAreaIdAndStatusIn(Long areaId, Collection<ReservationStatus> statuses);


    @Query("SELECT r FROM Reservation r WHERE r.area.id = :areaId " +
            "AND r.status IN ('CONFIRMED', 'ACTIVE') " +
            "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(
            @Param("areaId") Long areaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}