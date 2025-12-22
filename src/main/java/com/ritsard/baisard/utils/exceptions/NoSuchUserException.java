package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String message) {
        super(message);
    }
    public NoSuchUserException() {
        super("No such user exist");
    }

    public NoSuchUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
