package com.ritsard.baisard.utils.exceptions.crypto;

public class DecryptionException extends Exception {
    private String algorithm;

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecryptionException(String message, String algorithm) {
        super(message);
        this.algorithm = algorithm;
    }

    public DecryptionException(String message, String algorithm, Throwable cause) {
        super(message, cause);
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
