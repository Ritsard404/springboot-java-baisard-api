/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.ritsard.baisard.utils.validation.number.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={PasswordValidator.class})
public @interface ValidPassword {
    public String message() default "Invalid password";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

    public int minLength() default 6;

    public int minUpperCase() default 1;

    public int minLowerCase() default 1;

    public boolean allowSpecialCharacters() default true;
}

