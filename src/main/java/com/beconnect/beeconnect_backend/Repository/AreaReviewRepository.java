package com.beconnect.beeconnect_backend.Repository;

import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.AreaReview;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AreaReviewRepository extends JpaRepository<AreaReview, Long> {

    // Znajdź opinie dla obszaru
    List<AreaReview> findByAreaOrderByCreatedAtDesc(Area area);

    // Znajdź opinię dla konkretnej rezerwacji
    Optional<AreaReview> findByReservation(Reservation reservation);

    // Sprawdź czy rezerwacja ma już opinię
    boolean existsByReservation(Reservation reservation);

    // Znajdź opinie wystawione przez użytkownika
    List<AreaReview> findByReviewerOrderByCreatedAtDesc(Person reviewer);

    // Policz opinie dla obszaru
    long countByArea(Area area);

    // Średnia ocena dla obszaru
    @Query("SELECT AVG(ar.rating) FROM AreaReview ar WHERE ar.area = :area")
    Double getAverageRatingByArea(Area area);
}