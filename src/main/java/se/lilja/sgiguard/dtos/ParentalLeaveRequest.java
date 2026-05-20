package se.lilja.sgiguard.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParentalLeaveRequest {

    @NotNull
    @Pattern(regexp = "\\d{12}", message = "Felaktigt personnummer")
    private String personalNumber;

    @NotNull
    @FutureOrPresent(message="Date can not be in the past")
    LocalDate date;

    @NotNull
    @DecimalMax(value = "1.0", message="Extent must be between 0.125-1.0")
    @DecimalMin(value = "0.125", message="Extent must be between 0.125-1.0")
    Double extent;
}
