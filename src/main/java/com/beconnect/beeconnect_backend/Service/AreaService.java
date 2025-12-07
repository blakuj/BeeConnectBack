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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final PersonRepository personRepository;
    private final PersonService personService;
    private final GeometryFactory geometryFactory = new GeometryFactory(); // Fabryka geometrii

    @Autowired
    private FlowerRepository flowerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public AreaService(AreaRepository areaRepository, PersonRepository personRepository, PersonService personService) {
        this.areaRepository = areaRepository;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    private Set<Flower> processFlowers(Set<FlowerDTO> flowerDTOs) {
        Set<Flower> flowers = new HashSet<>();
        if (flowerDTOs != null) {
            for (FlowerDTO fDto : flowerDTOs) {
                if (fDto.getName() != null && !fDto.getName().trim().isEmpty()) {
                    Flower flower = flowerRepository.findByName(fDto.getName())
                            .orElseGet(() -> flowerRepository.save(
                                    Flower.builder()
                                            .name(fDto.getName())
                                            .color(fDto.getColor() != null ? fDto.getColor() : "#888888")
                                            .build()
                            ));
                    flowers.add(flower);
                }
            }
        }
        return flowers;
    }

    // Konwersja DTO (Lista List) -> JTS Polygon
    private Polygon createPolygonFromCoordinates(List<List<Double>> coordinatesDto) {
        if (coordinatesDto == null || coordinatesDto.size() < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 points");
        }

        // Konwertujemy listę list na tablicę Coordinate
        List<Coordinate> points = new ArrayList<>();
        for (List<Double> point : coordinatesDto) {
            // Leaflet wysyła [lat, lng], a JTS zazwyczaj oczekuje [x, y] (lng, lat)
            // Jednak w MSSQL dla typu geography kolejność to lat, long.
            // Dla typu geometry to x, y.
            // Zakładając spójność z frontendem, zachowajmy kolejność z DTO.
            points.add(new Coordinate(point.get(0), point.get(1)));
        }

        // Polygon musi być zamknięty (pierwszy punkt == ostatni punkt)
        if (!points.get(0).equals(points.get(points.size() - 1))) {
            points.add(points.get(0));
        }

        Coordinate[] coordinatesArray = points.toArray(new Coordinate[0]);
        LinearRing shell = geometryFactory.createLinearRing(coordinatesArray);
        return geometryFactory.createPolygon(shell);
    }

    // Konwersja JTS Polygon -> DTO (Lista List)
    private List<List<Double>> convertPolygonToDto(Polygon polygon) {
        if (polygon == null) return new ArrayList<>();

        List<List<Double>> result = new ArrayList<>();
        Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();

        // Pomijamy ostatni punkt, jeśli jest taki sam jak pierwszy (żeby nie dublować na froncie, choć Leaflet to zniesie)
        int length = coordinates.length;
        if (length > 1 && coordinates[0].equals(coordinates[length - 1])) {
            length--;
        }

        for (int i = 0; i < length; i++) {
            result.add(List.of(coordinates[i].x, coordinates[i].y));
        }
        return result;
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

        // Tworzenie poligonu
        Polygon polygon = createPolygonFromCoordinates(areaDto.getCoordinates());

        Area area = Area.builder()
                .flowers(flowers)
                .images(images)
                .polygon(polygon) // Zapisujemy jako geometry
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

            if (editAreaDTO.getImages() != null) {
                area.getImages().clear();
                List<Image> newImages = editAreaDTO.getImages().stream()
                        .map(base64 -> Image.builder().fileContent(base64).build())
                        .collect(Collectors.toList());
                area.getImages().addAll(newImages);
            }

            if (editAreaDTO.getFlowers() != null) {
                Set<Flower> newFlowers = processFlowers(editAreaDTO.getFlowers());
                area.setFlowers(newFlowers);
            }

            area.setDescription(editAreaDTO.getDescription());
            area.setEndDate(editAreaDTO.getEndDate());
            area.setPricePerDay(editAreaDTO.getPricePerDay());
            area.setMaxHives(editAreaDTO.getMaxHives());
            area.setAvailabilityStatus(editAreaDTO.getAvailabilityStatus());

            // Jeśli w EditAreaDTO przychodziłyby koordynaty, tu też trzeba by je zaktualizować na Polygon

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

        List<Reservation> activeReservations = reservationRepository.findByTenant(currentUser);

        return activeReservations.stream()
                .map(reservation -> {
                    Area area = reservation.getArea();
                    AreaDTO dto = this.mapToDTO(area);
                    dto.setReservationId(reservation.getId());
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
        List<List<Double>> coords = convertPolygonToDto(area.getPolygon());

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