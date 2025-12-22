/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.ritsard.baisard.utils.number.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator
implements ConstraintValidator<ValidPassword, String> {
    private int minLength;
    private int minUpperCase;
    private int minLowerCase;
    private boolean allowSpecialCharacters;

    public void initialize(ValidPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.minUpperCase = constraintAnnotation.minUpperCase();
        this.minLowerCase = constraintAnnotation.minLowerCase();
        this.allowSpecialCharacters = constraintAnnotation.allowSpecialCharacters();
    }

    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.length() < this.minLength) {
            return false;
        }
        int upperCaseCount = 0;
        int lowerCaseCount = 0;
        int specialCharCount = 0;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                ++upperCaseCount;
                continue;
            }
            if (Character.isLowerCase(c)) {
                ++lowerCaseCount;
                continue;
            }
            if (Character.isLetterOrDigit(c)) continue;
            ++specialCharCount;
        }
        boolean meetsUpperCaseReq = upperCaseCount >= this.minUpperCase;
        boolean meetsLowerCaseReq = lowerCaseCount >= this.minLowerCase;
        boolean meetsSpecialCharReq = this.allowSpecialCharacters || specialCharCount == 0;
        return meetsUpperCaseReq && meetsLowerCaseReq && meetsSpecialCharReq;
    }
}

