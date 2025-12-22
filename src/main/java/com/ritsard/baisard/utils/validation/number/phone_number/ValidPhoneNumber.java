/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.ritsard.baisard.utils.validation.number.phone_number;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={PhoneNumberValidator.class})
public @interface ValidPhoneNumber {
    public String message() default "\uc62c\ubc14\ub974\uc9c0 \uc54a\uc740 \uc804\ud654\ubc88\ud638 \ud615\uc2dd\uc785\ub2c8\ub2e4.";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public boolean onlyMobile() default false;
}

