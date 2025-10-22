package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.DTO.EditAreaDTO;
import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;

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

        List<Area> areas = areaRepository.findByOwner(owner);

        return areas.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AreaDTO> getAllAreas() {
        List<Area> areas = areaRepository.findAll();
        return areas.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private AreaDTO mapToDTO(Area area) {
        List<List<Double>> coords = area.getCoordinates().stream()
                .map(s -> {
                    String[] parts = s.split(",");
                    return List.of(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                })
                .collect(Collectors.toList());

        return new AreaDTO(
                area.getType(),
                coords,
                area.getArea(),
                area.getDescription(),
                area.getMaxHives(),
                area.getPricePerDay(),
                area.getStatus(),
                area.getOwner() != null ? area.getOwner().getFirstname() : null,
                area.getOwner() != null ? area.getOwner().getLastname() : null
        );
    }
}
