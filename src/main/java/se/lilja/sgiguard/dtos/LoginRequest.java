package se.lilja.sgiguard.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {

    @NotNull(message = "Personnummer saknas")
    @Pattern(regexp = "\\d{12}", message = "Felaktigt personnummer")
    private String username;

    @NotNull(message = "Lösenord saknas")
    @Pattern(
            regexp = "^{8,}$",
            message = "Lösenordet måste innehålla minst 8 tecken"
    )
    private String password;
}
