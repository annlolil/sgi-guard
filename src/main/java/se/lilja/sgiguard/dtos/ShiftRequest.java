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

    @NotNull
    @Pattern(regexp = "\\d{12}", message = "Felaktigt personnummer")
    private String personalNumber;

    @NotNull
    @FutureOrPresent(message="Date can not be in the past")
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent(message="Date can not be in the past")
    private LocalDate endDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private Integer breakMinutes;

    private String type;
}
