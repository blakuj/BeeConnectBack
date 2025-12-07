package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.AreaReview;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AreaReviewRepository extends JpaRepository<AreaReview, Long> {

    List<AreaReview> findByReservationAreaOrderByCreatedAtDesc(Area area);

    Optional<AreaReview> findByReservation(Reservation reservation);

    boolean existsByReservation(Reservation reservation);

    List<AreaReview> findByReservationTenantOrderByCreatedAtDesc(Person tenant);

    long countByReservationArea(Area area);

    @Query("SELECT AVG(ar.rating) FROM AreaReview ar WHERE ar.reservation.area = :area")
    Double getAverageRatingByArea(@Param("area") Area area);
}