package com.beconnect.beeconnect_backend.ServiceTest;

import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;

import com.beconnect.beeconnect_backend.Service.AreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AreaServiceTest {

    private AreaRepository areaRepository;
    private PersonRepository personRepository;
    private AreaService areaService;

    @BeforeEach
    void setUp() {
        areaRepository = mock(AreaRepository.class);
        personRepository = mock(PersonRepository.class);
        areaService = new AreaService(areaRepository, personRepository);
    }

    @Test
    void addAreaWhenUserExists() {
        // given
        Person person = new Person();
        person.setLogin("john");
        when(personRepository.findByLogin("john")).thenReturn(Optional.of(person));

        AreaDTO dto = new AreaDTO(
                "FIELD",
                List.of(List.of(12.34, 56.78), List.of(98.76, 54.32)),
                200.0,
                "Test description",
                10,
                5.5,
                null,
                null,
                null
        );

        // when
        areaService.addArea(dto, "john");

        // then
        ArgumentCaptor<Area> captor = ArgumentCaptor.forClass(Area.class);
        verify(areaRepository, times(1)).save(captor.capture());
        Area savedArea = captor.getValue();

        assertThat(savedArea.getType()).isEqualTo("FIELD");
        assertThat(savedArea.getCoordinates()).containsExactly("12.34,56.78", "98.76,54.32");
        assertThat(savedArea.getArea()).isEqualTo(200.0);
        assertThat(savedArea.getDescription()).isEqualTo("Test description");
        assertThat(savedArea.getMaxHives()).isEqualTo(10);
        assertThat(savedArea.getPricePerDay()).isEqualTo(5.5);
        assertThat(savedArea.getStatus()).isEqualTo("AVAILABLE");
        assertThat(savedArea.getOwner()).isEqualTo(person);
        assertThat(savedArea.getDateAdded()).isNotNull();
    }
    @Test
    void addAreaThrowExceptionWhenUserNotFound() {
        // given
        when(personRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        AreaDTO dto = new AreaDTO("FIELD", List.of(), 100.0, "desc", 5, 2.5, null, null, null);

        // when / then
        assertThatThrownBy(() -> areaService.addArea(dto, "unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void getAreasForUserReturnMappedDTOs() {
        // given
        Person person = new Person();
        person.setLogin("john");
        person.setFirstname("John");
        person.setLastname("Doe");

        Area area = Area.builder()
                .type("FOREST")
                .coordinates(List.of("1.0,2.0", "3.0,4.0"))
                .area(50.0)
                .description("Nice place")
                .maxHives(20)
                .pricePerDay(15.0)
                .status("AVAILABLE")
                .owner(person)
                .dateAdded(LocalDateTime.now())
                .build();

        when(personRepository.findByLogin("john")).thenReturn(Optional.of(person));
        when(areaRepository.findByOwner(person)).thenReturn(List.of(area));

        // when
        List<AreaDTO> result = areaService.getAreasForUser("john");

        // then
        assertThat(result).hasSize(1);
        AreaDTO dto = result.get(0);
        assertThat(dto.getType()).isEqualTo("FOREST");
        assertThat(dto.getCoordinates()).containsExactly(List.of(1.0, 2.0), List.of(3.0, 4.0));
        assertThat(dto.getArea()).isEqualTo(50.0);
        assertThat(dto.getDescription()).isEqualTo("Nice place");
        assertThat(dto.getMaxHives()).isEqualTo(20);
        assertThat(dto.getPricePerDay()).isEqualTo(15.0);
        assertThat(dto.getStatus()).isEqualTo("AVAILABLE");
        assertThat(dto.getOwnerFirstName()).isEqualTo("John");
        assertThat(dto.getOwnerLastName()).isEqualTo("Doe");
    }


}
