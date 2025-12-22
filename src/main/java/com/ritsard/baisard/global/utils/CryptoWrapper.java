package com.ritsard.baisard.global.utils;

import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.DecryptionException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CryptoWrapper {

    public static <T, R> Function<T, R> wrapSafeWithCrypto(FunctionWithCrypto<T, R> function) {
        return input -> {
            try {
                return function.apply(input);
            } catch (CryptoKeyException | DecryptionException e) {
                throw new RuntimeException("Decryption failed", e);
            }
        };
    }

    public static <T> UnaryOperator<T> wrapSafeEncryptionWithCrypto(UnaryOperatorWithEncryption<T> operator) {
        return input -> {
            try {
                return operator.apply(input);
            } catch (CryptoKeyException | EncryptionException e) {
                throw new RuntimeException("Crypto mutation failed", e);
            }
        };
    }

    @FunctionalInterface
    public interface FunctionWithCrypto<T, R> {
        R apply(T t) throws CryptoKeyException, DecryptionException;
    }

    @FunctionalInterface
    public interface UnaryOperatorWithEncryption<T> {
        T apply(T t) throws CryptoKeyException, EncryptionException;
    }
}

