package se.lilja.sgiguard.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWorkSummary {

    private double baselineHours;
    private double actualHours;

    private double leaveExtent;
    private double leaveHours;

    private double remainingGapHours;
    private double requiredLeaveHours;

    private double recommendedExtent;
}
