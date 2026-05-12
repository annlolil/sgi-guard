package se.lilja.sgiguard.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * Creates a DateRange object covering the full 24-hour periods for the given dates.
 * Sets the start time to 00:00:00 of the start date and the end time to
 * 00:00:00 of the end date.
 *
 * @param from The starting date of the period
 * @param to The ending date of the period
 * @return A DateRange with precise timestamps for the beginning and end of the period
 */
public record DateRange(LocalDateTime start, LocalDateTime end) {
    public DateRange {
        if (Duration.between(start, end).isNegative()) {
            throw new IllegalArgumentException("Invalid date range");
        }
    }

    public static DateRange of(LocalDate from, LocalDate to) {
        return new DateRange(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }
}
