package se.lilja.sgiguard.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SgiStatus {

    PROTECTED("Skyddad"),
    AT_RISK("Risk"),
    OVERCOMPENSATED("Överkompenserad");

    private final String displayName;

}
