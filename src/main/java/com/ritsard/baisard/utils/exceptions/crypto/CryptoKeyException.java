package com.ritsard.baisard.utils.exceptions.crypto;

public class CryptoKeyException extends Exception {
    private String keyType;

    public CryptoKeyException(String message) {
        super(message);
    }

    public CryptoKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoKeyException(String message, String keyType) {
        super(message);
        this.keyType = keyType;
    }

    public CryptoKeyException(String message, String keyType, Throwable cause) {
        super(message, cause);
        this.keyType = keyType;
    }

    public String getKeyType() {
        return keyType;
    }
}