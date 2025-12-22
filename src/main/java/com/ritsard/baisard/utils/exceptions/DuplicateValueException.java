package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class DuplicateValueException extends RuntimeException {
    public DuplicateValueException(String message) {
        super(message);
    }

    public DuplicateValueException() {
        super("Request value is already exist");
    }
}
