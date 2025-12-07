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

    @Transactional
    public AreaReviewDTO createReview(CreateAreaReviewDTO dto) {
        Person currentUser = personService.getProfile();

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getTenant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only review your own reservations");
        }

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("You can only review confirmed reservations");
        }

        if (reviewRepository.existsByReservation(reservation)) {
            throw new RuntimeException("You have already reviewed this reservation");
        }

        // ZMIANA: Nie ustawiamy już reviewer i area
        AreaReview review = AreaReview.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .reservation(reservation)
                .build();

        review = reviewRepository.save(review);

        updateAreaRating(reservation.getArea());

        return mapToDTO(review);
    }

    public List<AreaReviewDTO> getAreaReviews(Long areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found"));

        // ZMIANA: nowa metoda repozytorium
        List<AreaReview> reviews = reviewRepository.findByReservationAreaOrderByCreatedAtDesc(area);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AreaReviewDTO> getMyReviews() {
        Person currentUser = personService.getProfile();
        // ZMIANA: nowa metoda repozytorium
        List<AreaReview> reviews = reviewRepository.findByReservationTenantOrderByCreatedAtDesc(currentUser);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean canReviewReservation(Long reservationId) {
        // ... (bez zmian) ...
        Person currentUser = personService.getProfile();
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return false;
        if (!reservation.getTenant().getId().equals(currentUser.getId())) return false;
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) return false;
        return !reviewRepository.existsByReservation(reservation);
    }

    @Transactional
    public void updateAreaRating(Area area) {
        Double averageRating = reviewRepository.getAverageRatingByArea(area);
        // ZMIANA: nowa metoda repozytorium
        long reviewCount = reviewRepository.countByReservationArea(area);

        area.setAverageRating(averageRating != null ? averageRating : 0.0);
        area.setReviewCount((int) reviewCount);
        areaRepository.save(area);
    }

    private AreaReviewDTO mapToDTO(AreaReview review) {
        // ZMIANA: Pobieramy dane przez relację reservation
        Person reviewer = review.getReservation().getTenant();
        Area area = review.getReservation().getArea();

        return AreaReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewerId(reviewer.getId())
                .reviewerFirstname(reviewer.getFirstname())
                .reviewerLastname(reviewer.getLastname())
                .areaId(area.getId())
                .areaName(area.getName())
                .reservationId(review.getReservation().getId())
                .build();
    }
}