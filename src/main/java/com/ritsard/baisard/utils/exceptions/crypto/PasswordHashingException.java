package com.ritsard.baisard.utils.exceptions.crypto;

public class PasswordHashingException extends Exception {
    private String algorithm;

    public PasswordHashingException(String message) {
        super(message);
    }

    public PasswordHashingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordHashingException(String message, String algorithm) {
        super(message);
        this.algorithm = algorithm;
    }

    public PasswordHashingException(String message, String algorithm, Throwable cause) {
        super(message, cause);
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
