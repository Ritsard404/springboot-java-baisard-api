package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class IdentifierDuplicatedException extends RuntimeException {

    public IdentifierDuplicatedException() {
        super("Requested identifier is duplicated");
    }

    public IdentifierDuplicatedException(String message) {
        super(message);
    }

    public IdentifierDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
