package com.ritsard.baisard.file.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for encoding, decoding, and validating file IDs based on UUIDs.
 * Supports standard UUIDs and optionally UUIDv7 if available.
 */
@Component
public class FileIdUtils {
    private static final Logger log = LoggerFactory.getLogger(FileIdUtils.class);

    /**
     * Encode a UUID into a URL-safe Base64 string.
     */
    public static String encode(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
            byteBuffer.putLong(uuid.getMostSignificantBits());
            byteBuffer.putLong(uuid.getLeastSignificantBits());
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
            log.debug("UUID encoded: {} → {}", uuid, encoded);
            return encoded;
        } catch (Exception e) {
            log.error("UUID encoding failed: {}", uuid, e);
            throw new RuntimeException("UUID encoding failed", e);
        }
    }

    /**
     * Decode a URL-safe Base64 string into a UUID.
     */
    public static UUID decode(String encodedId) {
        if (encodedId == null || encodedId.isBlank()) {
            throw new IllegalArgumentException("File ID cannot be null or empty.");
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(encodedId);
            if (bytes.length != 16) {
                throw new IllegalArgumentException("Invalid file ID length: " + bytes.length);
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            long mostSigBits = byteBuffer.getLong();
            long leastSigBits = byteBuffer.getLong();
            UUID uuid = new UUID(mostSigBits, leastSigBits);
            log.debug("File ID decoded: {} → {}", encodedId, uuid);
            return uuid;
        } catch (IllegalArgumentException e) {
            log.warn("File ID decoding failed - invalid format: {}", encodedId);
            throw new IllegalArgumentException("Invalid file ID: " + encodedId, e);
        } catch (Exception e) {
            log.error("File ID decoding error: {}", encodedId, e);
            throw new RuntimeException("File ID decoding failed", e);
        }
    }

    /**
     * Validate whether a file ID string is correctly formatted and decodable.
     */
    public static boolean isValid(String encodedId) {
        if (encodedId == null || encodedId.isBlank()) {
            return false;
        }
        try {
            if (encodedId.length() != 22) {
                return false;
            }
            if (!encodedId.matches("^[A-Za-z0-9_-]+$")) {
                return false;
            }
            FileIdUtils.decode(encodedId);
            return true;
        } catch (Exception e) {
            log.debug("File ID validation failed: {}", encodedId);
            return false;
        }
    }

    /**
     * Check if all provided file IDs are valid.
     */
    public static boolean areAllValid(String... encodedIds) {
        if (encodedIds == null || encodedIds.length == 0) {
            return false;
        }
        for (String encodedId : encodedIds) {
            if (!FileIdUtils.isValid(encodedId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generate a new random file ID (UUID-based).
     */
    public static String generateNew() {
        UUID uuid = UUID.randomUUID();
        return FileIdUtils.encode(uuid);
    }

    /**
     * Generate a new UUIDv7-based file ID if available, fallback to standard UUID.
     */
    public static String generateNewV7() {
        try {
            Class<?> uuidManagerClass = Class.forName("com.lodong.utilsmodule.utils.UUIDManager");
            Method method = uuidManagerClass.getMethod("generateUUIDv7");
            UUID uuid = (UUID) method.invoke(null);
            return FileIdUtils.encode(uuid);
        } catch (Exception e) {
            log.warn("UUIDv7 generation failed, using standard UUID: {}", e.getMessage());
            return FileIdUtils.generateNew();
        }
    }

    /**
     * Extract timestamp from a UUIDv7-based file ID.
     * Returns -1 if not a UUIDv7 or extraction fails.
     */
    public static long extractTimestamp(String encodedId) {
        try {
            UUID uuid = FileIdUtils.decode(encodedId);
            if (uuid.version() == 7) {
                return uuid.getMostSignificantBits() >>> 16;
            }
            return -1L;
        } catch (Exception e) {
            log.debug("Timestamp extraction failed: {}", encodedId);
            return -1L;
        }
    }

    /**
     * Generate human-readable debug info for a file ID.
     */
    public static String getDebugInfo(String encodedId) {
        try {
            UUID uuid = FileIdUtils.decode(encodedId);
            long timestamp = FileIdUtils.extractTimestamp(encodedId);
            StringBuilder info = new StringBuilder();
            info.append("File ID: ").append(encodedId).append("\n");
            info.append("UUID: ").append(uuid).append("\n");
            info.append("Version: ").append(uuid.version()).append("\n");
            info.append("Length: ").append(encodedId.length()).append(" characters\n");
            if (timestamp > 0L) {
                info.append("Creation time: ").append(new Date(timestamp)).append("\n");
            }
            return info.toString();
        } catch (Exception e) {
            return "Invalid file ID: " + encodedId;
        }
    }
}
