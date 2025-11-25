package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.DTO.EditAreaDTO;
import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final PersonRepository personRepository;
    private final PersonService personService;

    @Autowired
    private ReservationRepository reservationRepository;

    public AreaService(AreaRepository areaRepository, PersonRepository personRepository, PersonService personService) {
        this.areaRepository = areaRepository;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    public void addArea(AreaDTO areaDto) {
        Person owner = personService.getProfile();

        Area area = Area.builder()
                .type(areaDto.getType())
                .coordinates(
                        areaDto.getCoordinates().stream()
                                .map(coord -> coord.get(0) + "," + coord.get(1))
                                .collect(Collectors.toList())
                )
                .area(areaDto.getArea())
                .description(areaDto.getDescription())
                .maxHives(areaDto.getMaxHives())
                .pricePerDay(areaDto.getPricePerDay())
                .availableFrom(LocalDate.from(LocalDateTime.now()))
                .owner(owner)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .build();

        areaRepository.save(area);
    }

    public List<AreaDTO> getOwnedAreas() {
        return personService.getProfile().getOwnedAreas()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AreaDTO> getRentedAreas() {
        Person currentUser = personService.getProfile();

        return currentUser.getRentedAreas()
                .stream()
                .map(area -> {
                    AreaDTO dto = this.mapToDTO(area);

                    // Znajdź rezerwację dla tego obszaru i użytkownika
                    Optional<Reservation> reservationOpt = reservationRepository
                            .findByAreaAndTenant(area, currentUser);

                    reservationOpt.ifPresent(reservation -> {
                        dto.setReservationId(reservation.getId());
                    });

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<AreaDTO> getAllAreas() {
        List<Area> areas = areaRepository.findAll();
        return areas.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void editArea(EditAreaDTO editAreaDTO) {
        Optional<Area> toEdit = areaRepository.findById(editAreaDTO.getId());

        toEdit.ifPresent(area -> {
            area.setName(editAreaDTO.getName());
            area.setImgBase64(editAreaDTO.getImgBase64());
            area.setType(editAreaDTO.getType());
            area.setDescription(editAreaDTO.getDescription());
            area.setEndDate(editAreaDTO.getEndDate());
            area.setPricePerDay(editAreaDTO.getPricePerDay());
            area.setMaxHives(editAreaDTO.getMaxHives());
            area.setAvailabilityStatus(editAreaDTO.getAvailabilityStatus());
            areaRepository.save(area);
        });
    }

    public void deleteArea(Long id) {
        Optional<Area> toDelete = areaRepository.findById(id);
        toDelete.ifPresent(areaRepository::delete);
    }

    // ✅ POPRAWIONA METODA mapToDTO - używa buildera
    private AreaDTO mapToDTO(Area area) {
        List<List<Double>> coords = area.getCoordinates().stream()
                .map(s -> {
                    String[] parts = s.split(",");
                    return List.of(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                })
                .collect(Collectors.toList());

        return AreaDTO.builder()
                .id(area.getId())
                .type(area.getType())
                .coordinates(coords)
                .area(area.getArea())
                .description(area.getDescription())
                .maxHives(area.getMaxHives())
                .pricePerDay(area.getPricePerDay())
                .status(AvailabilityStatus.valueOf(area.getAvailabilityStatus().toString()))
                .ownerFirstName(area.getOwner().getFirstname())
                .ownerLastName(area.getOwner().getLastname())
                .availableFrom(area.getAvailableFrom())
                .imgBase64(area.getImgBase64())
                .name(area.getName())
                .averageRating(area.getAverageRating() != null ? area.getAverageRating() : 0.0)
                .reviewCount(area.getReviewCount() != null ? area.getReviewCount() : 0)
                .build();
        // reservationId zostanie ustawione później w getRentedAreas()
    }
}