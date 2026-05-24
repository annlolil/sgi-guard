package se.lilja.sgiguard.dtos;

import lombok.*;
import se.lilja.sgiguard.models.SgiStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SgiDailyAnalysisResponse {

    private LocalDate analysisDate;
    private String dayOfWeek;

    private String baselineHours;
    private String actualHours;
    private String leaveExtent;

    private String remainingGapHours;
    private double recommendedExtent;
    private String recommendedExtentLabel;

    private SgiStatus status;
}
