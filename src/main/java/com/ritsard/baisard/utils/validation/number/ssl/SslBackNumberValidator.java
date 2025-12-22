/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.ritsard.baisard.utils.validation.number.ssl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class SslBackNumberValidator
implements ConstraintValidator<ValidSslBackNumber, String> {
    private static final List<Character> VALID_FIRST_DIGITS = Arrays.asList(Character.valueOf('1'), Character.valueOf('2'), Character.valueOf('3'), Character.valueOf('4'));

    public boolean isValid(String backNumber, ConstraintValidatorContext context) {
        if (backNumber == null || !backNumber.matches("\\d{7}")) {
            return false;
        }
        char firstDigit = backNumber.charAt(0);
        return VALID_FIRST_DIGITS.contains(Character.valueOf(firstDigit));
    }
}

