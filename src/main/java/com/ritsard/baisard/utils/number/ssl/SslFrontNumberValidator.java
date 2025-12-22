/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.ritsard.baisard.utils.number.ssl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SslFrontNumberValidator
implements ConstraintValidator<ValidSslFrontNumber, String> {
    public boolean isValid(String frontNumber, ConstraintValidatorContext context) {
        if (frontNumber == null || !frontNumber.matches("\\d{6}")) {
            return false;
        }
        return this.isValidBirthDate(frontNumber);
    }

    private boolean isValidBirthDate(String frontNumber) {
        String[] possibleYears;
        int year = Integer.parseInt(frontNumber.substring(0, 2));
        int month = Integer.parseInt(frontNumber.substring(2, 4));
        int day = Integer.parseInt(frontNumber.substring(4, 6));
        if (month < 1 || month > 12 || day < 1 || day > 31) {
            return false;
        }
        for (String fullYear : possibleYears = new String[]{"18" + year, "19" + year, "20" + year, "21" + year}) {
            try {
                LocalDate.parse(fullYear + String.format("%02d%02d", month, day), DateTimeFormatter.ofPattern("yyyyMMdd"));
                return true;
            }
            catch (DateTimeParseException dateTimeParseException) {
            }
        }
        return false;
    }
}

