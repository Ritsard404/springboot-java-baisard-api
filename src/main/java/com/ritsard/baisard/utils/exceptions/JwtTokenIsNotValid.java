package com.ritsard.baisard.utils.exceptions;

import java.lang.RuntimeException;

public class JwtTokenIsNotValid extends RuntimeException {
    public JwtTokenIsNotValid(String message) {
        super(message);
    }

    public JwtTokenIsNotValid(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenIsNotValid() {
        super("Token is not valid");
    }
}

