/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 */
package com.ritsard.baisard.utils.validation.number.phone_number;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator
implements ConstraintValidator<ValidPhoneNumber, String> {
    private boolean onlyMobile;
    private static final String MOBILE_PHONE_REGEX = "^(\\+82|0)(10|11|16|17|18|19)[-]?[0-9]{3,4}[-]?[0-9]{4}$";
    private static final String KOREAN_PHONE_REGEX = "^(\\+82|0)(10|2|[3-9][0-9])[-]?[0-9]{3,4}[-]?[0-9]{4}$";
    private static final String INTERNATIONAL_PHONE_REGEX = "^\\+(?:[1-9][0-9]{0,2})(?!0)[0-9]{6,12}$";

    public void initialize(ValidPhoneNumber constraintAnnotation) {
        this.onlyMobile = constraintAnnotation.onlyMobile();
    }

    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return false;
        }
        phoneNumber = phoneNumber.trim();
        if (this.onlyMobile) {
            return phoneNumber.matches(MOBILE_PHONE_REGEX);
        }
        if (phoneNumber.startsWith("+")) {
            return phoneNumber.matches(INTERNATIONAL_PHONE_REGEX);
        }
        return phoneNumber.matches(KOREAN_PHONE_REGEX);
    }
}

