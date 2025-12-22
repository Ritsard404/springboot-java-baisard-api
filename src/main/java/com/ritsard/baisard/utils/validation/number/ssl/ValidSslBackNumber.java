/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.ritsard.baisard.utils.validation.number.ssl;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={SslBackNumberValidator.class})
public @interface ValidSslBackNumber {
    public String message() default "Invalid back part of Resident Registration Number (7 digits, starting with 1~4)";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}

