package se.lilja.sgiguard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.lilja.sgiguard.models.SgiStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SgiDailyAnalysisResponse {

    private LocalDate date;
    private String dayOfWeek;

    private String baselineHours;
    private String actualHours;
    private String leaveExtent;

    private String remainingGapHours;
    private double recommendedExtent;
    private String recommendedExtentLabel;

    private SgiStatus status;
}
