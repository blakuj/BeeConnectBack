package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.DTO.EditAreaDTO;
import com.beconnect.beeconnect_backend.DTO.FlowerDTO;
import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Flower;
import com.beconnect.beeconnect_backend.Model.Image;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Reservation;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.FlowerRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final PersonRepository personRepository;
    private final PersonService personService;

    @Autowired
    private FlowerRepository flowerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public AreaService(AreaRepository areaRepository, PersonRepository personRepository, PersonService personService) {
        this.areaRepository = areaRepository;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    /**
     * Pomocnicza metoda do przetwarzania DTO kwiatów na encje Flower.
     * Zapobiega duplikatom w bazie - szuka po nazwie, a tworzy tylko gdy nie istnieje.
     */
    private Set<Flower> processFlowers(Set<FlowerDTO> flowerDTOs) {
        Set<Flower> flowers = new HashSet<>();
        if (flowerDTOs != null) {
            for (FlowerDTO fDto : flowerDTOs) {
                if (fDto.getName() != null && !fDto.getName().trim().isEmpty()) {
                    Flower flower = flowerRepository.findByName(fDto.getName())
                            .orElseGet(() -> flowerRepository.save(
                                    Flower.builder()
                                            .name(fDto.getName())
                                            .color(fDto.getColor() != null ? fDto.getColor() : "#888888") // Domyślny kolor
                                            .build()
                            ));
                    flowers.add(flower);
                }
            }
        }
        return flowers;
    }

    @Transactional
    public void addArea(AreaDTO areaDto) {
        Person owner = personService.getProfile();

        Set<Flower> flowers = processFlowers(areaDto.getFlowers());

        List<Image> images = new ArrayList<>();
        if (areaDto.getImages() != null) {
            images = areaDto.getImages().stream()
                    .map(base64 -> Image.builder().fileContent(base64).build())
                    .collect(Collectors.toList());
        }

        Area area = Area.builder()
                .flowers(flowers)
                .images(images)
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
                .name(areaDto.getName())
                .averageRating(0.0)
                .reviewCount(0)
                .build();

        areaRepository.save(area);
    }

    @Transactional
    public void editArea(EditAreaDTO editAreaDTO) {
        Optional<Area> toEdit = areaRepository.findById(editAreaDTO.getId());

        toEdit.ifPresent(area -> {
            area.setName(editAreaDTO.getName());

            // Aktualizacja zdjęć (podmiana listy)
            if (editAreaDTO.getImages() != null) {
                area.getImages().clear();
                List<Image> newImages = editAreaDTO.getImages().stream()
                        .map(base64 -> Image.builder().fileContent(base64).build())
                        .collect(Collectors.toList());
                area.getImages().addAll(newImages);
            }

            // Aktualizacja kwiatów (podmiana zbioru)
            if (editAreaDTO.getFlowers() != null) {
                Set<Flower> newFlowers = processFlowers(editAreaDTO.getFlowers());
                area.setFlowers(newFlowers);
            }

            area.setDescription(editAreaDTO.getDescription());
            area.setEndDate(editAreaDTO.getEndDate());
            area.setPricePerDay(editAreaDTO.getPricePerDay());
            area.setMaxHives(editAreaDTO.getMaxHives());
            area.setAvailabilityStatus(editAreaDTO.getAvailabilityStatus());

            areaRepository.save(area);
        });
    }

    public List<AreaDTO> getAllAreas() {
        List<Area> areas = areaRepository.findAll();
        return areas.stream().map(this::mapToDTO).collect(Collectors.toList());
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
                    // Dodaj ID rezerwacji jeśli istnieje
                    Optional<Reservation> reservationOpt = reservationRepository
                            .findByAreaAndTenant(area, currentUser);
                    reservationOpt.ifPresent(reservation -> {
                        dto.setReservationId(reservation.getId());
                    });
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteArea(Long id) {
        Optional<Area> toDelete = areaRepository.findById(id);
        toDelete.ifPresent(areaRepository::delete);
    }

    private AreaDTO mapToDTO(Area area) {
        List<List<Double>> coords = area.getCoordinates().stream()
                .map(s -> {
                    String[] parts = s.split(",");
                    return List.of(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                })
                .collect(Collectors.toList());

        List<String> images = area.getImages().stream()
                .map(Image::getFileContent)
                .collect(Collectors.toList());

        Set<FlowerDTO> flowerDTOs = area.getFlowers().stream()
                .map(f -> FlowerDTO.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .color(f.getColor())
                        .build())
                .collect(Collectors.toSet());

        return AreaDTO.builder()
                .id(area.getId())
                .flowers(flowerDTOs)
                .images(images)
                .coordinates(coords)
                .area(area.getArea())
                .description(area.getDescription())
                .maxHives(area.getMaxHives())
                .pricePerDay(area.getPricePerDay())
                .status(AvailabilityStatus.valueOf(area.getAvailabilityStatus().toString()))
                .ownerFirstName(area.getOwner().getFirstname())
                .ownerLastName(area.getOwner().getLastname())
                .availableFrom(area.getAvailableFrom())
                .name(area.getName())
                .averageRating(area.getAverageRating() != null ? area.getAverageRating() : 0.0)
                .reviewCount(area.getReviewCount() != null ? area.getReviewCount() : 0)
                .build();
    }
}