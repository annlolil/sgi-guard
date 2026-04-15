package se.lilja.sgiguard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.lilja.sgiguard.models.SgiStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SgiStatusResponse {

    private double plannedHours;
    private double targetHours;
    private double gapHours;
    private SgiStatus status;
    private String recommendation;
}
