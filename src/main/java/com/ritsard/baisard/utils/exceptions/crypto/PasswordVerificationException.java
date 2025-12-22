package com.ritsard.baisard.utils.exceptions.crypto;

public class PasswordVerificationException extends RuntimeException {
    public PasswordVerificationException(String message) {
        super(message);
    }

    public PasswordVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
