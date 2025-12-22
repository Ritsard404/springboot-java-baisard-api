/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.ritsard.baisard.utils.exceptions;

import org.springframework.security.core.AuthenticationException;

public class LoginNoSuchUserException
        extends AuthenticationException {
    public LoginNoSuchUserException(String message) {
        super(message);
    }

    public LoginNoSuchUserException() {
        super("User is not exist");
    }
}

