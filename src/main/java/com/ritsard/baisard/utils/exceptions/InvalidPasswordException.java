/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.ritsard.baisard.utils.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidPasswordException
extends AuthenticationException {
    public InvalidPasswordException() {
        super("Password does not match");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}

