package se.lilja.sgiguard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.lilja.sgiguard.models.SgiStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SgiPeriodAnalysisResponse {

    private List<SgiWeeklyAnalysisResponse> weeklyStatuses;
    private double totalPlannedHours;
    private double totalTargetHours;
    private SgiStatus overallStatus;
    private String periodRecommendation;
}
