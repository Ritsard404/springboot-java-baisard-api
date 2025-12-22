package com.ritsard.baisard.utils.exceptions.gpt;

public class GptException
extends RuntimeException {
    public GptException(String message) {
        super(message);
    }

    public GptException(String message, Throwable cause) {
        super(message, cause);
    }
}

