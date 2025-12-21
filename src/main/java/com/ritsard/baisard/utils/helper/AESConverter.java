package com.ritsard.baisard.utils.helper;

import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.DecryptionException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AESConverter {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;     // 96-bit IV
    private static final int GCM_TAG_LENGTH = 16;    // 128-bit TAG

    // High-performance SecureRandom instance
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Cache for key reuse
    private static final ConcurrentHashMap<String, SecretKeySpec> KEY_CACHE = new ConcurrentHashMap<>();

    // Encryption key loaded from environment variable
    @Value("${aes.secretKey}")
    private String secretKey;

    /**
     * Encrypts a string using AES.
     *
     * @param text Plaintext to encrypt
     * @return Base64-encoded ciphertext
     * @throws EncryptionException If encryption fails
     * @throws CryptoKeyException  If key generation fails
     */
    public String encryption(String text) throws EncryptionException, CryptoKeyException {
        if (text == null) return null;
        if (text.isEmpty()) return "";

        try {
            SecretKeySpec keySpec = getCachedKey();

            // Generate IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // Perform encryption
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

            // Combine [IV + ciphertext]
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(result);

        } catch (CryptoKeyException e) {
            // Propagate key-related exceptions
            throw e;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // Algorithm support error
            throw new EncryptionException(
                    String.format("Unsupported encryption algorithm: %s", AES_TRANSFORMATION),
                    AES_ALGORITHM, e
            );
        } catch (InvalidKeyException e) {
            // Invalid key
            throw new CryptoKeyException(
                    "Invalid encryption key",
                    "SECRET_KEY", e
            );
        } catch (InvalidAlgorithmParameterException e) {
            // Invalid parameters (GCM spec)
            throw new EncryptionException(
                    "Invalid encryption parameters",
                    AES_ALGORITHM, e
            );
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // Encryption processing error
            throw new EncryptionException(
                    "An error occurred during encryption processing",
                    AES_ALGORITHM, e
            );
        } catch (OutOfMemoryError e) {
            // Out of memory
            throw new EncryptionException(
                    "Insufficient memory for encryption",
                    AES_ALGORITHM, e
            );
        } catch (Exception e) {
            // Unexpected error
            throw new EncryptionException(
                    "An unexpected error occurred during encryption",
                    AES_ALGORITHM, e
            );
        }
    }

    /**
     * Decrypts the ciphertext and returns the original plaintext.
     *
     * @param encryptedText Base64-encoded ciphertext
     * @return Decrypted plaintext
     * @throws DecryptionException If decryption fails
     * @throws CryptoKeyException  If key generation fails
     */
    public String decryption(String encryptedText) throws DecryptionException, CryptoKeyException {
        if (encryptedText == null) return null;
        if (encryptedText.isEmpty()) return "";

        try {
            // Base64 decoding
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);

            // Minimum length validation
            if (encryptedWithIv.length < GCM_IV_LENGTH + GCM_TAG_LENGTH) {
                throw new DecryptionException(
                        String.format(
                                "Encrypted data is too short. Minimum %d bytes required (current: %d bytes)",
                                GCM_IV_LENGTH + GCM_TAG_LENGTH,
                                encryptedWithIv.length
                        ),
                        AES_ALGORITHM
                );
            }

            // Separate IV and ciphertext
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            SecretKeySpec keySpec = getCachedKey();

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // Perform decryption
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (CryptoKeyException e) {
            // Propagate key-related exceptions
            throw e;
        } catch (IllegalArgumentException e) {
            // Base64 decoding failure
            throw new DecryptionException(
                    "Invalid ciphertext format. Base64 decoding failed",
                    AES_ALGORITHM, e
            );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // Algorithm support error
            throw new DecryptionException(
                    String.format("Unsupported decryption algorithm: %s", AES_TRANSFORMATION),
                    AES_ALGORITHM, e
            );
        } catch (InvalidKeyException e) {
            // Invalid key
            throw new CryptoKeyException(
                    "Invalid decryption key",
                    "SECRET_KEY", e
            );
        } catch (InvalidAlgorithmParameterException e) {
            // Invalid parameters (GCM spec)
            throw new DecryptionException(
                    "Invalid decryption parameters",
                    AES_ALGORITHM, e
            );
        } catch (BadPaddingException e) {
            // Usually caused by wrong key or corrupted data
            throw new DecryptionException(
                    "Decryption failed: invalid key or corrupted data",
                    AES_ALGORITHM, e
            );
        } catch (IllegalBlockSizeException e) {
            // Block size error
            throw new DecryptionException(
                    "Decryption failed: invalid encrypted data size",
                    AES_ALGORITHM, e
            );
        } catch (OutOfMemoryError e) {
            // Out of memory
            throw new DecryptionException(
                    "Insufficient memory for decryption",
                    AES_ALGORITHM, e
            );
        } catch (Exception e) {
            // Unexpected error
            throw new DecryptionException(
                    "An unexpected error occurred during decryption",
                    AES_ALGORITHM, e
            );
        }
    }

    /**
     * Legacy decryption support (ECB, Hex) — migration use only.
     *
     * @param encryptedText Existing ciphertext (Hex-encoded AES/ECB)
     * @return Decrypted plaintext
     * @throws DecryptionException If decryption fails
     * @throws CryptoKeyException  If key generation fails
     * @deprecated ECB mode is insecure; migration to GCM mode is required
     */
    @Deprecated
    public String legacyDecryption(String encryptedText) throws DecryptionException, CryptoKeyException {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            // Legacy key generation (XOR-based)
            byte[] key = new byte[16];
            int i = 0;
            for (byte b : secretKey.getBytes(StandardCharsets.UTF_8)) {
                key[i++ % 16] ^= b;
            }

            SecretKeySpec keySpec = new SecretKeySpec(key, AES_ALGORITHM);

            // Decrypt using ECB mode
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // Hex decoding after decryption
            byte[] decoded = org.apache.commons.codec.binary.Hex.decodeHex(encryptedText.toCharArray());
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (org.apache.commons.codec.DecoderException e) {
            // Hex decoding failure
            throw new DecryptionException(
                    "Legacy decryption failed: invalid Hex format",
                    "AES/ECB", e
            );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // Algorithm error
            throw new DecryptionException(
                    "Legacy decryption failed: unsupported algorithm",
                    "AES/ECB", e
            );
        } catch (InvalidKeyException e) {
            // Key error
            throw new CryptoKeyException(
                    "Legacy decryption failed: invalid key",
                    "LEGACY_KEY", e
            );
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // Decryption processing error
            throw new DecryptionException(
                    "Legacy decryption failed: corrupted data or invalid key",
                    "AES/ECB", e
            );
        } catch (Exception e) {
            // Unexpected error
            throw new DecryptionException(
                    "Unexpected error occurred during legacy decryption",
                    "AES/ECB", e
            );
        }
    }

    /**
     * Encrypts multiple strings in bulk.
     *
     * @param texts Array of plaintext strings
     * @return Encrypted array
     * @throws EncryptionException If encryption fails
     * @throws CryptoKeyException  If key generation fails
     */
    public String[] bulkEncryption(String[] texts) throws EncryptionException, CryptoKeyException {
        if (texts == null || texts.length == 0) {
            return new String[0];
        }

        String[] results = new String[texts.length];
        SecretKeySpec keySpec = getCachedKey();

        int failedCount = 0;
        StringBuilder errorDetails = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            try {
                if (texts[i] == null || texts[i].isEmpty()) {
                    results[i] = texts[i];
                } else {
                    results[i] = encryptWithKey(texts[i], keySpec);
                }
            } catch (Exception e) {
                failedCount++;
                errorDetails.append(String.format("\n  - Index %d: %s", i, e.getMessage()));
                results[i] = null; // Mark failed items as null
            }
        }

        // Throw exception if any item failed
        if (failedCount > 0) {
            throw new EncryptionException(
                    String.format("Bulk encryption failed for %d items:%s",
                            failedCount, errorDetails),
                    AES_ALGORITHM
            );
        }

        return results;
    }

    /**
     * Encrypts a string using a given key (internal optimization).
     */
    private String encryptWithKey(String text, SecretKeySpec keySpec)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        byte[] result = new byte[GCM_IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH);
        System.arraycopy(encrypted, 0, result, GCM_IV_LENGTH, encrypted.length);

        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * Retrieves a cached key or generates a new one.
     */
    private SecretKeySpec getCachedKey() throws CryptoKeyException {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new CryptoKeyException(
                    "Encryption key is not configured. Please check aes.secretKey",
                    "SECRET_KEY"
            );
        }

        try {
            return KEY_CACHE.computeIfAbsent(secretKey, key -> {
                try {
                    return generateSecureKey(key);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("SHA-256 algorithm is not available", e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof NoSuchAlgorithmException) {
                throw new CryptoKeyException(
                        "Key generation failed: SHA-256 algorithm not supported",
                        "SECRET_KEY", e.getCause()
                );
            }
            throw new CryptoKeyException(
                    "Error occurred while accessing key cache",
                    "SECRET_KEY", e
            );
        }
    }

    /**
     * Generates a secure AES key using SHA-256 from the given key string.
     */
    private SecretKeySpec generateSecureKey(String key) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    /**
     * Checks whether the given string is a valid AES encrypted format.
     *
     * @param encryptedText Base64-encoded ciphertext
     * @return Whether the format is valid
     */
    public boolean isValidEncryptedFormat(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return false;
        }

        try {
            byte[] data = Base64.getDecoder().decode(encryptedText);
            return data.length >= GCM_IV_LENGTH + GCM_TAG_LENGTH;
        } catch (IllegalArgumentException e) {
            // Base64 decoding failure
            return false;
        }
    }

    /**
     * Clears the key cache (for security or testing purposes).
     */
    public static void clearKeyCache() {
        KEY_CACHE.clear();
    }

    /**
     * Returns the number of cached keys (for monitoring).
     */
    public static int getKeyCacheSize() {
        return KEY_CACHE.size();
    }
}
