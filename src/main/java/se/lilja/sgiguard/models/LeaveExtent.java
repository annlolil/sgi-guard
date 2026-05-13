package se.lilja.sgiguard.models;

import lombok.Getter;

@Getter
public enum LeaveExtent {

    ONE_EIGHTH(0.125),
    ONE_QUARTER(0.25),
    HALF(0.5),
    THREE_QUARTERS(0.75),
    FULL(1.0);

    private final double value;

    LeaveExtent(double value) {
        this.value = value;
    }
}
