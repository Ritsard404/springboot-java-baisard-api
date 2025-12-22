package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class InvalidTokenRequestException extends RuntimeException {
    public InvalidTokenRequestException(String message) {
        super(message);
    }

    public InvalidTokenRequestException() {
        super("This token is not valid");
    }

    public InvalidTokenRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
