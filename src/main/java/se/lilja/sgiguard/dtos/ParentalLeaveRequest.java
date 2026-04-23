package se.lilja.sgiguard.dtos;

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

    Long personId;
    LocalDate date;
    Double extent;
}
