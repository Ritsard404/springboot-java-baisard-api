/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions.gpt;
public class RateLimitException
extends GptException {
    private final int retryAfterSeconds;

    public RateLimitException(String message, int retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getRetryAfterSeconds() {
        return this.retryAfterSeconds;
    }
}

