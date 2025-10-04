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



}
