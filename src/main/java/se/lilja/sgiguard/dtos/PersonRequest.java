package se.lilja.sgiguard.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PersonRequest {

    @NotNull
    @Pattern(regexp = "\\d{12}", message = "Felaktigt personnummer")
    private String personalNumber;

    @NotBlank(message = "Förnamn får inte vara tomt")
    @Size(min = 2, message = "Namn får inte vara kortare än 3 tecken")
    @Pattern(regexp = "^[a-zA-ZåäöÅÄÖ\\s-]+$", message = "Namnet får bara innehålla bokstäver, mellanslag eller bindestreck")
    private String firstName;

    @NotBlank
    @Size(min = 2, message = "Namn får inte vara kortare än 3 tecken")
    @Pattern(regexp = "^[a-zA-ZåäöÅÄÖ\\s-]+$", message = "Namnet får bara innehålla bokstäver, mellanslag eller bindestreck")
    private String lastName;
}
