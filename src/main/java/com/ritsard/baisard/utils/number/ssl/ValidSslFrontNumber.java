/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.ritsard.baisard.utils.number.ssl;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={SslFrontNumberValidator.class})
public @interface ValidSslFrontNumber {
    public String message() default "Invalid front part of Resident Registration Number (YYMMDD format required)";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}

