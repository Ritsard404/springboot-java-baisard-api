package com.ritsard.baisard.utils.helper;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

public class UUIDManager {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generate UUID v7 (RFC 4122 draft compliant)
     * 48-bit timestamp + 4-bit version + 12-bit sequence + 2-bit variant + 62-bit random
     * @return UUID v7 object
     */
    public static UUID generateUUIDv7() {
        long timestamp = Instant.now().toEpochMilli();
        int sequence = SECURE_RANDOM.nextInt(1 << 12);
        long random = SECURE_RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL;

        long mostSigBits = (timestamp & 0xFFFFFFFFFFFFL) << 16;
        mostSigBits |= (0x7L << 12);
        mostSigBits |= (sequence & 0x0FFFL);

        long leastSigBits = (0x2L << 62) | random;

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Generate UUID v7 (legacy version for compatibility)
     * @return UUID v7 object
     * @deprecated Use generateUUIDv7() for RFC-compliant implementation
     */
    @Deprecated
    public static UUID generateUUIDv7Legacy() {
        long timestamp = Instant.now().toEpochMilli();
        int randomSequence = SECURE_RANDOM.nextInt(4096);

        long mostSigBits = (timestamp & 0x0000FFFFFFFFFFFFL) << 16;
        mostSigBits |= 0x7000L;
        mostSigBits |= (randomSequence & 0x0FFFL);

        long leastSigBits = SECURE_RANDOM.nextLong();
        leastSigBits &= 0x3FFFFFFFFFFFFFFFL;
        leastSigBits |= 0x8000000000000000L;

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Generate UUID v1
     * @return UUID v1 object
     */
    public static UUID generateUUID1() {
        long currentTimeMillis = System.currentTimeMillis();
        long time = currentTimeMillis * 10000 + 0x01B21DD213814000L;

        int clockSeq = SECURE_RANDOM.nextInt() & 0x3FFF;

        long nodeId = SECURE_RANDOM.nextLong() & 0x0000FFFFFFFFFFFFL;
        nodeId |= 0x010000000000L;

        long mostSigBits = (time & 0xFFFFFFFF00000000L) >>> 32 |
                (time & 0x00000000FFFF0000L) << 16 |
                (time & 0x000000000000FFFFL) << 48 |
                0x1000L;

        long leastSigBits = 0x8000000000000000L |
                ((long) clockSeq << 48) |
                nodeId;

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Generate random UUID as byte array
     * @return UUID byte array
     */
    public static byte[] generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuidToBytes(uuid);
    }

    /**
     * Convert UUID to byte array
     * @param uuid UUID to convert
     * @return byte array
     */
    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Convert byte array to MySQL binary string
     * @param bytes byte array to convert
     * @return MySQL binary string
     */
    public static String bytesToMysqlBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder("0x");
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Convert MySQL binary string to UUID
     * @param mysqlBinaryValue MySQL binary string
     * @return UUID object
     */
    public static UUID mysqlBinaryToUUID(String mysqlBinaryValue) {
        String hexString = mysqlBinaryValue.startsWith("0x") ?
                mysqlBinaryValue.substring(2) : mysqlBinaryValue;

        if (hexString.length() != 32) {
            throw new IllegalArgumentException("Invalid MySQL binary UUID format: " + mysqlBinaryValue);
        }

        byte[] bytes = hexStringToByteArray(hexString);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Convert hex string to byte array
     * @param hexString hex string
     * @return byte array
     */
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len++;
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int digit1 = Character.digit(hexString.charAt(i), 16);
            int digit2 = Character.digit(hexString.charAt(i + 1), 16);

            if (digit1 == -1 || digit2 == -1) {
                throw new IllegalArgumentException("Invalid hex character in string: " + hexString);
            }

            data[i / 2] = (byte) ((digit1 << 4) + digit2);
        }
        return data;
    }

    /**
     * Convert byte array to hex string
     * @param byteArray byte array
     * @return hex string
     */
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder result = new StringBuilder();
        for (byte b : byteArray) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    /**
     * Convert UUID to long integer (hash-based)
     * @param uuid UUID to convert
     * @return long integer
     */
    public static long toInt(UUID uuid) {
        try {
            byte[] uuidBytes = uuidToBytes(uuid);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(uuidBytes);

            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.put(hashed, 0, Long.BYTES);
            buffer.flip();
            return buffer.getLong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not found", e);
        }
    }

    /**
     * Extract timestamp from UUID v7
     * @param uuid UUID v7 object
     * @return millisecond timestamp
     * @throws IllegalArgumentException if UUID is not version 7
     */
    public static long extractTimestampFromUUIDv7(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();

        if ((mostSigBits & 0xF000L) != 0x7000L) {
            throw new IllegalArgumentException("UUID is not version 7");
        }

        return (mostSigBits >>> 16) & 0x0000FFFFFFFFFFFFL;
    }
}
