package com.beconnect.beeconnect_backend.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationDTO {
    @NotNull(message = "ID obszaru jest wymagane")
    private Long areaId;

    @NotNull(message = "Data rozpoczęcia jest wymagana")
    @FutureOrPresent(message = "Data rozpoczęcia nie może być w przeszłości")
    private LocalDate startDate;

    @NotNull(message = "Data zakończenia jest wymagana")
    @Future(message = "Data zakończenia musi być w przyszłości")
    private LocalDate endDate;

    @NotNull(message = "Liczba uli jest wymagana")
    @Min(value = 1, message = "Musisz zarezerwować przynajmniej 1 ul")
    private Integer numberOfHives;

    private String notes;
}