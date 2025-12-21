package com.ritsard.baisard.utils.helper;

import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.DecryptionException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA converter that automatically encrypts/decrypts string fields in entities.
 * <p>
 * A JPA Converter that supports both legacy (ECB) and modern (GCM) encryption.
 * <p>
 * - On persist: always encrypts using the new GCM method
 * - On read: automatically detects the format and decrypts using the appropriate method
 */
@Slf4j
@Component
@Converter
public class EncryptedFieldConverter implements AttributeConverter<String, String> {

    private static AESConverter aesConverter;

    @Autowired
    public void setAesConverter(AESConverter aesConverter) {
        EncryptedFieldConverter.aesConverter = aesConverter;
        log.info("EncryptedFieldConverter initialized successfully");
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            return attribute;
        }

        if (aesConverter == null) {
            throw new IllegalStateException(
                    "AESConverter is not initialized. Check Spring context."
            );
        }

        try {
            // Always encrypt using GCM
            return aesConverter.encryption(attribute);
        } catch (EncryptionException | CryptoKeyException e) {
            log.error("Encryption failed: {}", e.getMessage());
            throw new RuntimeException("Error occurred during data encryption", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return dbData;
        }

        if (aesConverter == null) {
            throw new IllegalStateException(
                    "AESConverter is not initialized. Check Spring context."
            );
        }

        try {
            // Detect encryption format automatically
            if (isNewFormat(dbData)) {
                log.trace("Decrypting as GCM format: {}", dbData.substring(0, Math.min(10, dbData.length())));
                return aesConverter.decryption(dbData);
            } else {
                log.trace("Decrypting as legacy format: {}", dbData.substring(0, Math.min(10, dbData.length())));
                return aesConverter.legacyDecryption(dbData);
            }
        } catch (Exception e) {
            log.warn("Initial decryption failed: {}", e.getMessage());

            // Retry with alternative format
            try {
                if (isNewFormat(dbData)) {
                    log.warn("GCM decryption failed, retrying with legacy format");
                    return aesConverter.legacyDecryption(dbData);
                } else {
                    log.warn("Legacy decryption failed, retrying with GCM format");
                    return aesConverter.decryption(dbData);
                }
            } catch (Exception retryException) {
                log.error("Retry decryption also failed: {}", retryException.getMessage());
                throw new RuntimeException("Error occurred during data decryption", e);
            }
        }
    }

    /**
     * Detects the encryption format of the given string.
     *
     * @param encryptedText Encrypted text
     * @return true if GCM/Base64 format, false if legacy ECB/Hex format
     */
    private boolean isNewFormat(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return true;
        }

        // Base64 may contain '+', '/', '='
        if (encryptedText.contains("+") || encryptedText.contains("/") || encryptedText.contains("=")) {
            return true;
        }

        // Base64 tends to be longer
        if (encryptedText.length() > 100) {
            return true;
        }

        // Hex format check (0-9, A-F only)
        return !encryptedText.matches("^[0-9A-Fa-f]+$");
    }
}
