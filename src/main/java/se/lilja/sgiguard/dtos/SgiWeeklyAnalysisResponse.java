package se.lilja.sgiguard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.lilja.sgiguard.models.SgiStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SgiWeeklyAnalysisResponse {

    private int weeklyNumber;
    private Map<DayOfWeek, SgiStatus> dailyStatus; // Showing weekday and if that day is protected, at_risk or overcompensated
    private Map<DayOfWeek, Double> dailyRecommendation; // Showing weekday and the recommendation of extent to apply for

//    private double actualWorkHours;
//    private double leaveHours;
//    private double totalPlanned;
//
//    private double gapHours;
//    private double recommendedDays;
//
//    private SgiStatus status;
//    private String recommendation;
//    private String warning;
}


