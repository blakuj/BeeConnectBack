package com.beconnect.beeconnect_backend.Repository;
import com.beconnect.beeconnect_backend.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}