/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.exceptions.gpt;

public class TokenLimitExceededException
extends GptException {
    private final int currentTokenCount;
    private final int maxTokens;

    public TokenLimitExceededException(String message, int currentTokenCount, int maxTokens) {
        super(message);
        this.currentTokenCount = currentTokenCount;
        this.maxTokens = maxTokens;
    }

    public int getCurrentTokenCount() {
        return this.currentTokenCount;
    }

    public int getMaxTokens() {
        return this.maxTokens;
    }
}

