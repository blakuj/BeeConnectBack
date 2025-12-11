package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.CreateReservationDTO;
import com.beconnect.beeconnect_backend.DTO.ReservationResponseDTO;
import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Enum.ReservationStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Utwórz i NATYCHMIAST POTWIERDŹ rezerwację (uproszczony proces)
     */
    @Transactional
    public ReservationResponseDTO createReservation(CreateReservationDTO dto) {
        Person tenant = personService.getProfile();

        // Walidacja danych wejściowych
        validateReservationData(dto);

        // Pobierz obszar
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RuntimeException("Area not found"));

        if (area.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new RuntimeException("Area is currently disabled by the owner");
        }

        // Sprawdź czy użytkownik nie jest właścicielem
        if (area.getOwner().getId().equals(tenant.getId())) {
            throw new RuntimeException("You cannot reserve your own area");
        }

        // Sprawdź czy liczba uli nie przekracza limitu
        if (dto.getNumberOfHives() > area.getMaxHives()) {
            throw new RuntimeException("Number of hives exceeds area limit (max: " + area.getMaxHives() + ")");
        }

        // Sprawdź czy nie ma nakładających się rezerwacji
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                area.getId(), dto.getStartDate(), dto.getEndDate());

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Selected dates overlap with existing reservations");
        }

        // Oblicz liczbę dni i całkowity koszt
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("End date must be after start date");
        }


        BigDecimal pricePerDay = BigDecimal.valueOf(area.getPricePerDay());
        BigDecimal daysBD = BigDecimal.valueOf(days);
        BigDecimal totalPrice = pricePerDay.multiply(daysBD);

        // Sprawdź saldo użytkownika
        if (tenant.getBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance. Required: " + totalPrice + " PLN, Available: " + tenant.getBalance() + " PLN");
        }

        tenant.setBalance(tenant.getBalance().subtract(totalPrice));
        personRepository.save(tenant);

        Person owner = area.getOwner();
        owner.setBalance(owner.getBalance().add(totalPrice));
        personRepository.save(owner);

        Reservation reservation = Reservation.builder()
                .area(area)
                .tenant(tenant)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .numberOfHives(dto.getNumberOfHives())
                .totalPrice(totalPrice)
                .pricePerDay(BigDecimal.valueOf(area.getPricePerDay()))
                .status(ReservationStatus.CONFIRMED)
                .confirmedAt(LocalDateTime.now())
                .notes(dto.getNotes())
                .build();

        reservation = reservationRepository.save(reservation);

        Person currentUser = personService.getProfile();

        notificationService.notifyAreaReserved(
                area.getOwner().getId(),
                area.getName(),
                currentUser.getFirstname() + " " + currentUser.getLastname(),
                reservation.getId()
        );

        return mapToDTO(reservation);
    }

    /**
     * Anuluj rezerwację
     */
    @Transactional
    public ReservationResponseDTO cancelReservation(Long reservationId, String reason) {
        Person currentUser = personService.getProfile();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Sprawdź uprawnienia
        boolean isTenant = reservation.getTenant().getId().equals(currentUser.getId());
        boolean isOwner = reservation.getArea().getOwner().getId().equals(currentUser.getId());

        if (!isTenant && !isOwner) {
            throw new RuntimeException("You don't have permission to cancel this reservation");
        }

        // Można anulować tylko CONFIRMED lub ACTIVE
        if (reservation.getStatus() != ReservationStatus.CONFIRMED &&
                reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new RuntimeException("Only confirmed or active reservations can be cancelled");
        }

        // Zwróć środki
        Person tenant = reservation.getTenant();
        Person owner = reservation.getArea().getOwner();

        BigDecimal refundAmount = reservation.getTotalPrice();

        // tenant + refund
        tenant.setBalance(tenant.getBalance().add(refundAmount));
        // owner - refund
        owner.setBalance(owner.getBalance().subtract(refundAmount));

        personRepository.save(tenant);
        personRepository.save(owner);


        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(reason);

        reservation = reservationRepository.save(reservation);

        return mapToDTO(reservation);
    }

    // ... (metody getMyReservations, getReservationById bez zmian) ...
    public List<ReservationResponseDTO> getMyReservations() {
        Person tenant = personService.getProfile();
        List<Reservation> reservations = reservationRepository.findByTenant(tenant);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getMyReservationsByStatus(ReservationStatus status) {
        Person tenant = personService.getProfile();
        List<Reservation> reservations = reservationRepository.findByTenantAndStatus(tenant, status);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getReservationsForMyAreas() {
        Person owner = personService.getProfile();
        List<Area> myAreas = areaRepository.findByOwner(owner);

        return myAreas.stream()
                .flatMap(area -> reservationRepository.findByArea(area).stream())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReservationResponseDTO getReservationById(Long id) {
        Person currentUser = personService.getProfile();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        boolean isTenant = reservation.getTenant().getId().equals(currentUser.getId());
        boolean isOwner = reservation.getArea().getOwner().getId().equals(currentUser.getId());

        if (!isTenant && !isOwner) {
            throw new RuntimeException("You don't have permission to view this reservation");
        }

        return mapToDTO(reservation);
    }

    /**
     * Zaktualizuj statusy rezerwacji - tylko ACTIVE -> COMPLETED
     */
    @Transactional
    public void updateReservationStatuses() {
        LocalDate today = LocalDate.now();

        // Zakończ aktywne rezerwacje, które się skończyły
        List<Reservation> toComplete = reservationRepository.findActiveReservationsEndingBefore(today);

        for (Reservation reservation : toComplete) {
            reservation.setStatus(ReservationStatus.COMPLETED);

            reservationRepository.save(reservation);
        }

        // Aktywuj potwierdzone rezerwacje, których czas nadszedł
        List<Reservation> toActivate = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> !r.getStartDate().isAfter(today))
                .collect(Collectors.toList());

        for (Reservation reservation : toActivate) {
            reservation.setStatus(ReservationStatus.ACTIVE);
            reservationRepository.save(reservation);
        }
    }

    /**
     * Pobierz listę wszystkich zajętych dat dla danego obszaru
     */
    public List<String> getOccupiedDates(Long areaId) {
        List<Reservation> reservations = reservationRepository.findByAreaIdAndStatusIn(
                areaId,
                List.of(ReservationStatus.CONFIRMED, ReservationStatus.ACTIVE)
        );

        Set<String> occupiedDates = new HashSet<>();

        for (Reservation res : reservations) {
            LocalDate start = res.getStartDate();
            LocalDate end = res.getEndDate();

            start.datesUntil(end.plusDays(1))
                    .forEach(date -> occupiedDates.add(date.toString()));
        }

        return new ArrayList<>(occupiedDates);
    }

    private void validateReservationData(CreateReservationDTO dto) {
        if (dto.getAreaId() == null) {
            throw new RuntimeException("Area ID is required");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new RuntimeException("Start and end dates are required");
        }
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate()) || dto.getEndDate().isEqual(dto.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        if (dto.getNumberOfHives() == null || dto.getNumberOfHives() <= 0) {
            throw new RuntimeException("Number of hives must be greater than 0");
        }
    }

    private ReservationResponseDTO mapToDTO(Reservation reservation) {
        Area area = reservation.getArea();
        Person tenant = reservation.getTenant();
        Person owner = area.getOwner();

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .areaId(area.getId())
                .areaName(area.getName())
                .areaType(area.getFlowers().stream().findFirst().map(f -> f.getName()).orElse("Nieznany"))
                .tenantId(tenant.getId())
                .tenantFirstname(tenant.getFirstname())
                .tenantLastname(tenant.getLastname())
                .tenantEmail(tenant.getEmail())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .numberOfHives(reservation.getNumberOfHives())
                .totalPrice(reservation.getTotalPrice().doubleValue())
                .pricePerDay(reservation.getPricePerDay().doubleValue())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .confirmedAt(reservation.getConfirmedAt())
                .cancelledAt(reservation.getCancelledAt())
                .notes(reservation.getNotes())
                .cancellationReason(reservation.getCancellationReason())
                .ownerFirstname(owner.getFirstname())
                .ownerLastname(owner.getLastname())
                .ownerEmail(owner.getEmail())
                .ownerPhone(owner.getPhone())
                .build();
    }
}