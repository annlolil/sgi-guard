package se.lilja.sgiguard.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnalyzeWeekRequest {

    @NotNull(message = "Välj ett datum")
    LocalDate date;
}
