package com.ritsard.baisard.utils.exceptions.crypto;

public class EncryptionException extends Exception {
    private String algorithm;

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(String message, String algorithm) {
        super(message);
        this.algorithm = algorithm;
    }

    public EncryptionException(String message, String algorithm, Throwable cause) {
        super(message, cause);
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}