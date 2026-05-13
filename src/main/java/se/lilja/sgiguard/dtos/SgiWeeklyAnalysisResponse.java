package se.lilja.sgiguard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SgiWeeklyAnalysisResponse {

    private int weeklyNumber;
    private List<SgiDailyAnalysisResponse> dailyAnalyses;
}


