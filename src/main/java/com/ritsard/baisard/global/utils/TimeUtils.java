package com.ritsard.baisard.global.utils;

import java.time.*;

public class TimeUtils {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * Converts a LocalDateTime that represents a Korean time to an Instant in UTC
     * This should be used when receiving times from clients that send KST times
     */
    public static Instant koreanTimeToUtc(LocalDateTime koreanTime) {
        return ZonedDateTime.of(koreanTime, SEOUL_ZONE).toInstant();
    }

    /**
     * Converts a LocalDate that represents a Korean date to an Instant in UTC
     * at the start of the day (00:00) KST.
     */
    public static Instant koreanTimeToUtc(LocalDate koreanDate) {
        LocalDateTime startOfDayKst = koreanDate.atStartOfDay();
        return koreanTimeToUtc(startOfDayKst);
    }

    /**
     * Converts an Instant to LocalDateTime in Korean time (KST/UTC+9)
     * This should be used when displaying times to users or doing calculations based on Korean business hours
     */
    public static LocalDateTime utcToKoreanTime(Instant utcTime) {
        if (utcTime == null) {
            return null;
        }
        return utcTime.atZone(SEOUL_ZONE).toLocalDateTime();
    }

    /**
     * Creates an Instant from year, month, day, hour, minute in Korean time
     */
    public static Instant createKoreanInstant(int year, int month, int day, int hour, int minute) {
        LocalDateTime koreanTime = LocalDateTime.of(year, month, day, hour, minute);
        return koreanTimeToUtc(koreanTime);
    }

    /**
     * Gets the current time in Korean timezone (KST/UTC+9)
     */
    public static LocalDateTime getCurrentKoreanTime() {
        return LocalDateTime.now(SEOUL_ZONE);
    }
}
