package com.ritsard.baisard.utils.converter.cryptions.aes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import com.ritsard.baisard.utils.helper.AESConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AesCryptoComponent {
    private static final Logger log = LoggerFactory.getLogger(AesCryptoComponent.class);
    private static AESConverter aesConverter;

    @Autowired
    public void setAesConverter(AESConverter aesConverter) {
        AesCryptoComponent.aesConverter = aesConverter;
        // Translation: AesCryptoComponent initialization complete
        log.info("AesCryptoComponent initialization complete");
    }

    private static boolean isNewFormat(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return true;
        }
        // Heuristic check for Base64 (new GCM format) vs Hex (legacy)
        if (encryptedText.contains("+") || encryptedText.contains("/") || encryptedText.contains("=")) {
            return true;
        }
        if (encryptedText.length() > 100) {
            return true;
        }
        return !encryptedText.matches("^[0-9A-Fa-f]+$");
    }

    public static class AesDecryptionSerializer
            extends JsonSerializer<String> {
        public void serialize(String encryptedValue, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (encryptedValue == null || encryptedValue.isEmpty()) {
                gen.writeNull();
                return;
            }
            if (aesConverter == null) {
                // Translation: AESConverter has not been initialized
                throw new IOException("AESConverter has not been initialized");
            }
            try {
                if (AesCryptoComponent.isNewFormat(encryptedValue)) {
                    // Translation: Decrypting using GCM format
                    log.trace("Decrypting using GCM format: {}", (Object) encryptedValue.substring(0, Math.min(10, encryptedValue.length())));
                    String decrypted = aesConverter.decryption(encryptedValue);
                    gen.writeString(decrypted);
                } else {
                    // Translation: Decrypting using legacy format
                    log.trace("Decrypting using legacy format: {}", (Object) encryptedValue.substring(0, Math.min(10, encryptedValue.length())));
                    String decrypted = aesConverter.legacyDecryption(encryptedValue);
                    gen.writeString(decrypted);
                }
            } catch (Exception e) {
                // Translation: Decryption failed
                log.error("Decryption failed: {}", (Object) e.getMessage());
                try {
                    if (AesCryptoComponent.isNewFormat(encryptedValue)) {
                        // Translation: GCM decryption failed, attempting legacy method
                        log.warn("GCM decryption failed, attempting legacy method");
                        String decrypted = aesConverter.legacyDecryption(encryptedValue);
                        gen.writeString(decrypted);
                    } else {
                        // Translation: Legacy decryption failed, attempting GCM method
                        log.warn("Legacy decryption failed, attempting GCM method");
                        String decrypted = aesConverter.decryption(encryptedValue);
                        gen.writeString(decrypted);
                    }
                } catch (Exception retryException) {
                    // Translation: Retry decryption failed
                    log.error("Retry decryption failed: {}", (Object) retryException.getMessage());
                    // Translation: [Decryption Failed]
                    gen.writeString("[Decryption Failed]");
                }
            }
        }
    }

    public static class AesEncryptionDeserializer
            extends JsonDeserializer<String> {
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String plainText = p.getValueAsString();
            if (plainText == null || plainText.isEmpty()) {
                return plainText;
            }
            if (aesConverter == null) {
                // Translation: AESConverter has not been initialized
                throw new IOException("AESConverter has not been initialized");
            }
            try {
                return aesConverter.encryption(plainText);
            } catch (Exception e) {
                // Translation: Encryption failed / An error occurred during encryption processing
                log.error("Encryption failed: {}", (Object) e.getMessage());
                throw new IOException("An error occurred during encryption processing", e);
            }
        }
    }
}