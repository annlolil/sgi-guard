package se.lilja.sgiguard.models;

public enum ShiftType {

    /* Explanation of shift types
    - BASELINE are shifts that a person should have worked in their original schedule
    but are not going to work due to a lowered percentage of employment rate
    - ACTUAL are shifts that a person should have worked and also are planned to work
     */

    BASELINE,
    ACTUAL
}
