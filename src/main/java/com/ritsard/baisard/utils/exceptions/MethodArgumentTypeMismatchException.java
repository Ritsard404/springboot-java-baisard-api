/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class MethodArgumentTypeMismatchException
extends RuntimeException {
    private final String parameterName;
    private final Object value;
    private final Class<?> requiredType;

    public MethodArgumentTypeMismatchException(String message) {
        super(message);
        this.parameterName = null;
        this.value = null;
        this.requiredType = null;
    }

    public MethodArgumentTypeMismatchException(String parameterName, Object value, Class<?> requiredType) {
        super(String.format("\ud30c\ub77c\ubbf8\ud130 '%s'\uc758 \ud0c0\uc785\uc774 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \uc608\uc0c1: %s, \uc2e4\uc81c: %s", parameterName, requiredType != null ? requiredType.getSimpleName() : "Unknown", value != null ? value.getClass().getSimpleName() : "null"));
        this.parameterName = parameterName;
        this.value = value;
        this.requiredType = requiredType;
    }

    public MethodArgumentTypeMismatchException(String message, Throwable cause) {
        super(message, cause);
        this.parameterName = null;
        this.value = null;
        this.requiredType = null;
    }

    public MethodArgumentTypeMismatchException(String parameterName, Object value, Class<?> requiredType, Throwable cause) {
        super(String.format("\ud30c\ub77c\ubbf8\ud130 '%s'\uc758 \ud0c0\uc785\uc774 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \uc608\uc0c1: %s, \uc2e4\uc81c: %s", parameterName, requiredType != null ? requiredType.getSimpleName() : "Unknown", value != null ? value.getClass().getSimpleName() : "null"), cause);
        this.parameterName = parameterName;
        this.value = value;
        this.requiredType = requiredType;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public Object getValue() {
        return this.value;
    }

    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    @Override
    public String toString() {
        return String.format("MethodArgumentTypeMismatchException{parameterName='%s', value=%s, requiredType=%s, message='%s'}", this.parameterName, this.value, this.requiredType != null ? this.requiredType.getName() : "null", this.getMessage());
    }
}

