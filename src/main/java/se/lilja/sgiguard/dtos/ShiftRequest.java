package se.lilja.sgiguard.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

//    private Long employmentId;
    @NotNull
    @Pattern(regexp = "\\d{12}", message = "Felaktigt personnummer")
    private String personalNumber;

    @NotNull
//    @FutureOrPresent(message="Date can not be in the past") // outcommented for testing
    private LocalDate startDate;

    @NotNull
//    @FutureOrPresent(message="Date can not be in the past") // outcommented for testing
    private LocalDate endDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private Integer breakMinutes;

    private String type;
}
