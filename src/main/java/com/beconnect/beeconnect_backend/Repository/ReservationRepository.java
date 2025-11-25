package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Znajdź wszystkie rezerwacje użytkownika
    List<Reservation> findByTenant(Person tenant);

    // Znajdź rezerwacje według statusu dla użytkownika
    List<Reservation> findByTenantAndStatus(Person tenant, ReservationStatus status);

    Optional<Reservation> findByAreaAndTenant(Area area, Person tenant);

    // Znajdź wszystkie rezerwacje dla danego obszaru
    List<Reservation> findByArea(Area area);

    // Znajdź aktywne rezerwacje dla obszaru
    List<Reservation> findByAreaAndStatus(Area area, ReservationStatus status);

    // Sprawdź czy są nakładające się rezerwacje dla obszaru
    @Query("SELECT r FROM Reservation r WHERE r.area.id = :areaId " +
            "AND r.status IN ('CONFIRMED', 'ACTIVE') " +
            "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(
            @Param("areaId") Long areaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Znajdź rezerwacje kończące się wkrótce (do automatycznej zmiany statusu)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' " +
            "AND r.endDate < :date")
    List<Reservation> findActiveReservationsEndingBefore(@Param("date") LocalDate date);

    // Policz rezerwacje według statusu
    long countByStatus(ReservationStatus status);
}