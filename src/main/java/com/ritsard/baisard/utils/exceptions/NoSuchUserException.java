package com.ritsard.baisard.utils.exceptions;

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
