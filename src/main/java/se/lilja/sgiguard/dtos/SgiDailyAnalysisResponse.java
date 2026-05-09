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
    private DayOfWeek dayOfWeek;

    private double baselineHours;
    private double actualHours;
    private double leaveExtent;

    private double gapHours;
    private double recommendedExtent;

    private SgiStatus status;
}
