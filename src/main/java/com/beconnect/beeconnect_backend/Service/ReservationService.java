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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

        // Sprawdź czy obszar jest dostępny
        if (area.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new RuntimeException("Area is not available for reservation");
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

        double totalPrice = days * area.getPricePerDay();

        // Sprawdź saldo użytkownika
        if (tenant.getBalance() < totalPrice) {
            throw new RuntimeException("Insufficient balance. Required: " + totalPrice + " PLN, Available: " + tenant.getBalance() + " PLN");
        }

        // POBIERZ ŚRODKI OD RAZU
        tenant.setBalance((float) (tenant.getBalance() - totalPrice));
        personRepository.save(tenant);

        // DODAJ ŚRODKI WŁAŚCICIELOWI
        Person owner = area.getOwner();
        owner.setBalance((float) (owner.getBalance() + totalPrice));
        personRepository.save(owner);

        // Utwórz rezerwację jako CONFIRMED od razu
        Reservation reservation = Reservation.builder()
                .area(area)
                .tenant(tenant)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .numberOfHives(dto.getNumberOfHives())
                .totalPrice(totalPrice)
                .pricePerDay(area.getPricePerDay())
                .status(ReservationStatus.CONFIRMED)
                .confirmedAt(LocalDateTime.now())
                .notes(dto.getNotes())
                .build();

        reservation = reservationRepository.save(reservation);

        // Zaktualizuj status obszaru na UNAVAILABLE
        area.setTenant(tenant);
        area.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
        areaRepository.save(area);

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

        tenant.setBalance(tenant.getBalance() + reservation.getTotalPrice().floatValue());
        owner.setBalance(owner.getBalance() - reservation.getTotalPrice().floatValue());

        personRepository.save(tenant);
        personRepository.save(owner);

        // Uwolnij obszar
        Area area = reservation.getArea();
        area.setTenant(null);
        area.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        areaRepository.save(area);

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(reason);

        reservation = reservationRepository.save(reservation);

        return mapToDTO(reservation);
    }

    /**
     * Pobierz wszystkie rezerwacje użytkownika (jako najemca)
     */
    public List<ReservationResponseDTO> getMyReservations() {
        Person tenant = personService.getProfile();
        List<Reservation> reservations = reservationRepository.findByTenant(tenant);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz rezerwacje według statusu
     */
    public List<ReservationResponseDTO> getMyReservationsByStatus(ReservationStatus status) {
        Person tenant = personService.getProfile();
        List<Reservation> reservations = reservationRepository.findByTenantAndStatus(tenant, status);
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz rezerwacje dla obszarów użytkownika (jako właściciel)
     */

    public List<ReservationResponseDTO> getReservationsForMyAreas() {
        Person owner = personService.getProfile();
        List<Area> myAreas = areaRepository.findByOwner(owner);

        return myAreas.stream()
                .flatMap(area -> reservationRepository.findByArea(area).stream())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz szczegóły rezerwacji
     */
    public ReservationResponseDTO getReservationById(Long id) {
        Person currentUser = personService.getProfile();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Sprawdź uprawnienia
        boolean isTenant = reservation.getTenant().getId().equals(currentUser.getId());
        boolean isOwner = reservation.getArea().getOwner().getId().equals(currentUser.getId());

        if (!isTenant && !isOwner) {
            throw new RuntimeException("You don't have permission to view this reservation");
        }

        return mapToDTO(reservation);
    }

    /**
     * Zaktualizuj statusy rezerwacji - tylko ACTIVE → COMPLETED
     */
    @Transactional
    public void updateReservationStatuses() {
        LocalDate today = LocalDate.now();

        // Zakończ aktywne rezerwacje, które się skończyły
        List<Reservation> toComplete = reservationRepository.findActiveReservationsEndingBefore(today);

        for (Reservation reservation : toComplete) {
            reservation.setStatus(ReservationStatus.COMPLETED);

            // Uwolnij obszar
            Area area = reservation.getArea();
            area.setTenant(null);
            area.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            areaRepository.save(area);

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
     * Walidacja danych rezerwacji
     */
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

    /**
     * Mapowanie Reservation → ReservationResponseDTO
     */
    private ReservationResponseDTO mapToDTO(Reservation reservation) {
        Area area = reservation.getArea();
        Person tenant = reservation.getTenant();
        Person owner = area.getOwner();

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .areaId(area.getId())
                .areaName(area.getName())
                .areaType(area.getType())
                .tenantId(tenant.getId())
                .tenantFirstname(tenant.getFirstname())
                .tenantLastname(tenant.getLastname())
                .tenantEmail(tenant.getEmail())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .numberOfHives(reservation.getNumberOfHives())
                .totalPrice(reservation.getTotalPrice())
                .pricePerDay(reservation.getPricePerDay())
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