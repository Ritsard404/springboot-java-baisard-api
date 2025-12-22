package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }

    public JwtTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenExpiredException() {
        super("Jwt Token is expired");
    }
}
