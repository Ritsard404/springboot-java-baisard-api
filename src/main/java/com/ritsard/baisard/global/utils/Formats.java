package com.ritsard.baisard.global.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Formats {

    private static final DecimalFormat PESO_FORMAT = new DecimalFormat("₱#,##0.00");
    private static final DecimalFormat PESO_FORMAT_ANDROID = new DecimalFormat("P#,##0.00");

    // Invoice ID format (12 digits, leading zeros)
    public static String invoiceFormat(long id) {
        return String.format("%012d", id);
    }

    // Date format: yyyy-MM-dd
    public static String dateFormat(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // DateTime format: yyyy-MM-dd hh:mm:ss a
    public static String dateTimeFormat(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));
    }

    // Round BigDecimal to 2 decimal places
    public static BigDecimal storeDecimalValueFormat(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    // Return rounded BigDecimal as String
    public static String storeDecimalStringValueFormat(BigDecimal value) {
        if (value == null) value = BigDecimal.ZERO;
        return storeDecimalValueFormat(value).toString();
    }

    // Format as Peso string (default non-Android)
    public static String pesoFormat(BigDecimal value) {
        if (value == null) value = BigDecimal.ZERO;
        return PESO_FORMAT.format(storeDecimalValueFormat(value));
    }

    // Peso format for Android
    public static String pesoFormat(BigDecimal value, boolean isAndroid) {
        if (value == null) value = BigDecimal.ZERO;
        return isAndroid
                ? PESO_FORMAT_ANDROID.format(storeDecimalValueFormat(value))
                : PESO_FORMAT.format(storeDecimalValueFormat(value));
    }


    public static String capitalize(String value) {
        if (value == null || value.isBlank()) return value;

        String[] words = value.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }
}