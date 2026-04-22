package se.lilja.sgiguard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.lilja.sgiguard.entities.ParentalLeave;
import se.lilja.sgiguard.models.SgiStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SgiWeeklyAnalysisResponse {

    private int week;

    private double plannedWorkHours;
    private double plannedParentalLeaveHours;
    private double totalPlannedHours;

    private double targetHours;
    private double gapHours;
    private double recommendedDays;

    private SgiStatus status;
    private String recommendation;
}
