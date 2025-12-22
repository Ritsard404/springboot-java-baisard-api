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
        log.info("AesCryptoComponent \ucd08\uae30\ud654 \uc644\ub8cc");
    }

    private static boolean isNewFormat(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return true;
        }
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
                throw new IOException("AESConverter\uac00 \ucd08\uae30\ud654\ub418\uc9c0 \uc54a\uc558\uc2b5\ub2c8\ub2e4");
            }
            try {
                if (AesCryptoComponent.isNewFormat(encryptedValue)) {
                    log.trace("GCM \ud615\uc2dd\uc73c\ub85c \ubcf5\ud638\ud654: {}", (Object) encryptedValue.substring(0, Math.min(10, encryptedValue.length())));
                    String decrypted = aesConverter.decryption(encryptedValue);
                    gen.writeString(decrypted);
                } else {
                    log.trace("\ub808\uac70\uc2dc \ud615\uc2dd\uc73c\ub85c \ubcf5\ud638\ud654: {}", (Object) encryptedValue.substring(0, Math.min(10, encryptedValue.length())));
                    String decrypted = aesConverter.legacyDecryption(encryptedValue);
                    gen.writeString(decrypted);
                }
            } catch (Exception e) {
                log.error("\ubcf5\ud638\ud654 \uc2e4\ud328: {}", (Object) e.getMessage());
                try {
                    if (AesCryptoComponent.isNewFormat(encryptedValue)) {
                        log.warn("GCM \ubcf5\ud638\ud654 \uc2e4\ud328, \ub808\uac70\uc2dc \ubc29\uc2dd \uc2dc\ub3c4");
                        String decrypted = aesConverter.legacyDecryption(encryptedValue);
                        gen.writeString(decrypted);
                    } else {
                        log.warn("\ub808\uac70\uc2dc \ubcf5\ud638\ud654 \uc2e4\ud328, GCM \ubc29\uc2dd \uc2dc\ub3c4");
                        String decrypted = aesConverter.decryption(encryptedValue);
                        gen.writeString(decrypted);
                    }
                } catch (Exception retryException) {
                    log.error("\uc7ac\uc2dc\ub3c4 \ubcf5\ud638\ud654\ub3c4 \uc2e4\ud328: {}", (Object) retryException.getMessage());
                    gen.writeString("[\ubcf5\ud638\ud654 \uc2e4\ud328]");
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
                throw new IOException("AESConverter\uac00 \ucd08\uae30\ud654\ub418\uc9c0 \uc54a\uc558\uc2b5\ub2c8\ub2e4");
            }
            try {
                return aesConverter.encryption(plainText);
            } catch (Exception e) {
                log.error("\uc554\ud638\ud654 \uc2e4\ud328: {}", (Object) e.getMessage());
                throw new IOException("\uc554\ud638\ud654 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4", e);
            }
        }
    }
}

