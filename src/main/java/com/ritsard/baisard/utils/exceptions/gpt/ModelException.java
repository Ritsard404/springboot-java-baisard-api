package com.ritsard.baisard.utils.exceptions.gpt;

public class ModelException
        extends GptException {
    public ModelException(String message) {
        super(message);
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
}

