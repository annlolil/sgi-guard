package se.lilja.sgiguard.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.lilja.sgiguard.models.ShiftType;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShiftRequest {

    @NotNull(message = "Välj startdatum")
    private LocalDate startDate;

    @NotNull(message = "Välj slutdatum")
    private LocalDate endDate;

    @NotNull(message = "Välj starttid")
    private LocalTime startTime;

    @NotNull(message = "Välj sluttid")
    private LocalTime endTime;

    @NotNull(message = "Rast får inte vara tom, om ingen rast fyll i 0")
    @Min(value = 0, message = "Om ingen rast, fyll i 0")
    private Integer breakMinutes;

    private String type;
}
