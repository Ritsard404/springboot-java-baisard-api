package com.ritsard.baisard.global.utils;

import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.DecryptionException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import com.ritsard.baisard.utils.helper.AESConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AESUtil {
    private final AESConverter aesConverter;

    public String decryptSafely(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.trim().isEmpty()) {
            return null;
        }

        try {
            return aesConverter.decryption(encryptedValue);
        } catch (CryptoKeyException | DecryptionException e) {
            log.warn("Failed to decrypt value: {}", e.getMessage());
            return encryptedValue;
        }
    }

    public String encryptSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return aesConverter.encryption(value);
        } catch (CryptoKeyException | EncryptionException e) {
            log.warn("Failed to encrypt value: {}", e.getMessage());
            return value;
        }
    }

    public String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.trim().isEmpty()) {
            return null;
        }
        try {
            return aesConverter.decryption(encryptedValue);
        } catch (CryptoKeyException | DecryptionException e) {
            log.error("Decryption failed: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public String encrypt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return aesConverter.encryption(value);
        } catch (CryptoKeyException | EncryptionException e) {
            log.error("Encryption failed: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }
}
