package se.lilja.sgiguard.models;

import lombok.Getter;

@Getter
public enum Warning {

    INVALID_EXTENT("Ogiltig omfattning"),
    WEEKEND_RULE("Helgregeln är inte uppfylld"),
    EXTENT_EXCEEDS_GAP("Omfattning överstiger saknade timmar"),
    EXTENT_EXCEEDS_DAY("Omfattning överstiger 1 dag"),
    WEEK_SCHEDULE_MISSING("Lägg till pass nästa vecka för att verifiera helgregeln");


    private final String message;

    Warning(String message) {
        this.message = message;
    }

}
