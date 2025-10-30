package com.beconnect.beeconnect_backend.Repository;
import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {



    List<Reservation> findByTenant(Person tenant);

    List<Reservation> findByTenantAndStatus(Person tenant, ReservationStatus status);

    List<Reservation> findByArea(Area area);

    List<Reservation> findByAreaAndStatus(Area area, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.area.id = :areaId " +
            "AND r.status IN ('CONFIRMED', 'ACTIVE') " +
            "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(
            @Param("areaId") Long areaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}