/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions;


import com.ritsard.baisard.utils.exceptions.gpt.GptException;

public class ApiKeyException
        extends GptException {
    public ApiKeyException(String message) {
        super(message);
    }

    public ApiKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}

