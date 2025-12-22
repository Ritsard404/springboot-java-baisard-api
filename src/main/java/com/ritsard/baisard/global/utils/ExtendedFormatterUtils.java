package com.ritsard.baisard.global.utils;

import com.ritsard.baisard.utils.helper.FormatterUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ExtendedFormatterUtils extends FormatterUtils {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    public static String formatPeriod(Instant start, Instant end) {
        if (start == null && end == null) {
            return "N/A";
        }
        if (start == null) {
            return "Until " + DATE_FORMATTER.format(end);
        }
        if (end == null) {
            return "From " + DATE_FORMATTER.format(start);
        }
        return DATE_FORMATTER.format(start) + " ~ " + DATE_FORMATTER.format(end);
    }

    public static String formatDate(Instant date) {
        if (date == null) return "N/A";
        return DATE_FORMATTER.format(date);
    }
}
