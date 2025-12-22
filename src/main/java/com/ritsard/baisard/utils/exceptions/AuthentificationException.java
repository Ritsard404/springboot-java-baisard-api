/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class AuthentificationException
extends RuntimeException {
    public AuthentificationException() {
        super("Authentification exception");
    }

    public AuthentificationException(String message) {
        super(message);
    }

    public AuthentificationException(String message, Throwable cause) {
        super(message, cause);
    }
}

