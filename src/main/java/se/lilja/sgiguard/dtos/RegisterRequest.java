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
public class RegisterRequest {

    @NotNull(message = "Personnummer saknas")
    @Pattern(regexp = "\\d{12}", message = "Felaktigt format, måste vara xxxxxxxxxxxx")
    private String personalNumber;

    @NotBlank(message = "Förnamn saknas")
    @Size(min = 2, message = "Förnamn får inte vara kortare än 3 tecken")
    @Pattern(regexp = "^[a-zA-ZåäöÅÄÖ\\s-]+$", message = "Namnet får bara innehålla bokstäver, mellanslag eller bindestreck")
    private String firstName;

    @NotBlank(message = "Efternamn saknas")
    @Size(min = 2, message = "Efternamn får inte vara kortare än 3 tecken")
    @Pattern(regexp = "^[a-zA-ZåäöÅÄÖ\\s-]+$", message = "Namnet får bara innehålla bokstäver, mellanslag eller bindestreck")
    private String lastName;

    @NotNull(message = "Lösenord saknas")
    @Size(min=8, message = "Lösenord måste vara minst 8 tecken långt")
    private String password;
}
