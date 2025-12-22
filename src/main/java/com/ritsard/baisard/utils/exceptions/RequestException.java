/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions;

public class RequestException
extends NetworkException {
    private final int statusCode;

    public RequestException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public RequestException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean isBadRequest() {
        return this.statusCode == 400;
    }

    public boolean isUnauthorized() {
        return this.statusCode == 401;
    }

    public boolean isForbidden() {
        return this.statusCode == 403;
    }

    public boolean isNotFound() {
        return this.statusCode == 404;
    }

    public boolean isRateLimited() {
        return this.statusCode == 429;
    }
}

