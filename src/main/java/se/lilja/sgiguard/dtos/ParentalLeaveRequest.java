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

    @NotNull(message = "Välj datum")
    LocalDate date;

    @NotNull(message = "Omfattning måste väljas")
    @DecimalMax(value = "1.0", message="Omfattning måste vara mellan 0.125-1.0")
    @DecimalMin(value = "0.125", message="Omfattning måste vara mellan 0.125-1.0")
    Double extent;
}
