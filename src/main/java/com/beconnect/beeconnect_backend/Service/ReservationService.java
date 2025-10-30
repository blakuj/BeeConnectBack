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


    @Transactional
    public ReservationResponseDTO createReservation(CreateReservationDTO dto) {
        Person tenant = personService.getProfile();

        validateReservationData(dto);

        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RuntimeException("Area not found"));

        if (area.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new RuntimeException("Area is not available for reservation");
        }

        if (area.getOwner().getId().equals(tenant.getId())) {
            throw new RuntimeException("You cannot reserve your own area");
        }

        if (dto.getNumberOfHives() > area.getMaxHives()) {
            throw new RuntimeException("Number of hives exceeds area limit (max: " + area.getMaxHives() + ")");
        }

        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                area.getId(), dto.getStartDate(), dto.getEndDate());

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Selected dates overlap with existing reservations");
        }

        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("End date must be after start date");
        }

        double totalPrice = days * area.getPricePerDay();

        if (tenant.getBalance() < totalPrice) {
            throw new RuntimeException("Insufficient balance. Required: " + totalPrice + " PLN, Available: " + tenant.getBalance() + " PLN");
        }

        tenant.setBalance((float) (tenant.getBalance() - totalPrice));
        personRepository.save(tenant);

        Person owner = area.getOwner();
        owner.setBalance((float) (owner.getBalance() + totalPrice));
        personRepository.save(owner);

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

        area.setTenant(tenant);
        area.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
        areaRepository.save(area);

        return mapToDTO(reservation);
    }

}