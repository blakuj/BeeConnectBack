package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.AreaReviewDTO;
import com.beconnect.beeconnect_backend.DTO.CreateAreaReviewDTO;
import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.AreaReview;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.AreaReviewRepository;
import com.beconnect.beeconnect_backend.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AreaReviewService {

    @Autowired
    private AreaReviewRepository reviewRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PersonService personService;

    /**
     * Utwórz opinię o obszarze
     */
    @Transactional
    public AreaReviewDTO createReview(CreateAreaReviewDTO dto) {
        Person currentUser = personService.getProfile();

        // Walidacja
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Pobierz rezerwację
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Sprawdź czy rezerwacja należy do użytkownika
        if (!reservation.getTenant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only review your own reservations");
        }

        // Sprawdź czy rezerwacja jest zakończona
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("You can only review confirmed reservations");
        }

        // Sprawdź czy rezerwacja już się zakończyła (endDate minęła)
//        if (reservation.getEndDate().isAfter(LocalDate.now())) {
//            throw new RuntimeException("You can only review reservations that have ended");
//        }

        // Sprawdź czy już nie wystawiono opinii
        if (reviewRepository.existsByReservation(reservation)) {
            throw new RuntimeException("You have already reviewed this reservation");
        }

        // Utwórz opinię
        AreaReview review = AreaReview.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .reviewer(currentUser)
                .area(reservation.getArea())
                .reservation(reservation)
                .build();

        review = reviewRepository.save(review);

        // Aktualizuj rating obszaru
        updateAreaRating(reservation.getArea());

        return mapToDTO(review);
    }

    /**
     * Pobierz opinie dla obszaru
     */
    public List<AreaReviewDTO> getAreaReviews(Long areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found"));

        List<AreaReview> reviews = reviewRepository.findByAreaOrderByCreatedAtDesc(area);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz opinie użytkownika
     */
    public List<AreaReviewDTO> getMyReviews() {
        Person currentUser = personService.getProfile();
        List<AreaReview> reviews = reviewRepository.findByReviewerOrderByCreatedAtDesc(currentUser);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sprawdź czy rezerwacja może być oceniona
     */
    public boolean canReviewReservation(Long reservationId) {
        Person currentUser = personService.getProfile();

        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return false;

        // Sprawdź czy rezerwacja należy do użytkownika
        if (!reservation.getTenant().getId().equals(currentUser.getId())) {
            return false;
        }

        // Sprawdź czy rezerwacja jest potwierdzona i zakończona
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return false;
        }

//        if (reservation.getEndDate().isAfter(LocalDate.now())) {
//            return false;
//        }

        // Sprawdź czy już nie wystawiono opinii
        return !reviewRepository.existsByReservation(reservation);
    }

    /**
     * Aktualizuj średni rating i liczbę opinii dla obszaru
     */
    @Transactional
    public void updateAreaRating(Area area) {
        Double averageRating = reviewRepository.getAverageRatingByArea(area);
        long reviewCount = reviewRepository.countByArea(area);

        area.setAverageRating(averageRating != null ? averageRating : 0.0);
        area.setReviewCount((int) reviewCount);
        areaRepository.save(area);
    }

    /**
     * Mapowanie AreaReview → AreaReviewDTO
     */
    private AreaReviewDTO mapToDTO(AreaReview review) {
        return AreaReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewerId(review.getReviewer().getId())
                .reviewerFirstname(review.getReviewer().getFirstname())
                .reviewerLastname(review.getReviewer().getLastname())
                .areaId(review.getArea().getId())
                .areaName(review.getArea().getName())
                .reservationId(review.getReservation().getId())
                .build();
    }
}