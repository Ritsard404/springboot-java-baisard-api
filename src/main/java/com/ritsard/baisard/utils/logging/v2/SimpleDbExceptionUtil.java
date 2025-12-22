/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  org.hibernate.exception.DataException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.orm.jpa.JpaSystemException
 */
package com.ritsard.baisard.utils.logging.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleDbExceptionUtil {
    private static final Logger log = LoggerFactory.getLogger(SimpleDbExceptionUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, Object> processDbException(Exception ex) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String errorId = EnhancedLoggingUtil.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        try {
            Throwable rootCause = EnhancedLoggingUtil.getRootCause(ex);
            String errorMessage = rootCause.getMessage();
            String exceptionType = rootCause.getClass().getSimpleName();
            log.error("\ud83d\udcbe DB \uc608\uc678 \ubc1c\uc0dd [ID:{}] {} - {}", new Object[]{errorId, exceptionType, errorMessage});
            String userMessage = "\ub370\uc774\ud130\ubca0\uc774\uc2a4 \uc791\uc5c5 \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
            String errorCode = "DATABASE_ERROR";
            if (ex instanceof JpaSystemException || ex instanceof DataException || rootCause instanceof SQLException) {
                int sqlErrorCode = -1;
                String sqlState = null;
                if (rootCause instanceof SQLException) {
                    SQLException sqlEx = (SQLException)rootCause;
                    sqlErrorCode = sqlEx.getErrorCode();
                    sqlState = sqlEx.getSQLState();
                    log.error("\ud83d\udcca SQL \uc815\ubcf4 - \ucf54\ub4dc: {}, \uc0c1\ud0dc: {}", (Object)sqlErrorCode, (Object)sqlState);
                }
                if (errorMessage.contains("Data truncated") || sqlErrorCode == 1406) {
                    String columnName = SimpleDbExceptionUtil.extractColumnName(errorMessage);
                    userMessage = columnName != null ? String.format("\uceec\ub7fc '%s'\uc5d0 \ub108\ubb34 \uae34 \ub370\uc774\ud130\uac00 \uc785\ub825\ub418\uc5c8\uc2b5\ub2c8\ub2e4.", columnName) : "\ub370\uc774\ud130 \uae38\uc774\uac00 \ucd5c\ub300 \ud5c8\uc6a9 \uae38\uc774\ub97c \ucd08\uacfc\ud588\uc2b5\ub2c8\ub2e4.";
                    errorCode = "DATA_TRUNCATION_ERROR";
                } else if (errorMessage.contains("Duplicate entry") || sqlErrorCode == 1062 || ex instanceof DataIntegrityViolationException && errorMessage.contains("ConstraintViolationException")) {
                    String constraintName = SimpleDbExceptionUtil.extractConstraintName(errorMessage);
                    String entity = SimpleDbExceptionUtil.extractEntityName(errorMessage, constraintName);
                    userMessage = entity != null ? String.format("'%s' \ub370\uc774\ud130\uac00 \uc774\ubbf8 \uc874\uc7ac\ud569\ub2c8\ub2e4.", entity) : "\uc911\ubcf5\ub41c \ub370\uc774\ud130\uac00 \uc874\uc7ac\ud569\ub2c8\ub2e4.";
                    errorCode = "DUPLICATE_ERROR";
                } else if (errorMessage.toLowerCase().contains("foreign key") || errorMessage.contains("foreign key constraint fails") || sqlErrorCode == 1452) {
                    userMessage = "\ucc38\uc870\ud558\ub294 \ub370\uc774\ud130\uac00 \uc874\uc7ac\ud558\uc9c0 \uc54a\uac70\ub098, \uc774\ubbf8 \ucc38\uc870\ub41c \ub370\uc774\ud130\ub97c \uc0ad\uc81c\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
                    errorCode = "FOREIGN_KEY_VIOLATION_ERROR";
                }
            }
            EnhancedLoggingUtil.logJumpableStackTrace(ex);
            result.put("errorId", errorId);
            result.put("message", userMessage);
            result.put("errorCode", errorCode);
            result.put("exceptionType", exceptionType);
            result.put("rootCause", rootCause.getClass().getName());
            HashMap<String, Object> hashMap = result;
            return hashMap;
        }
        finally {
            MDC.remove((String)"errorId");
        }
    }

    private static String extractColumnName(String message) {
        int endIndex;
        int startIndex;
        if (message == null) {
            return null;
        }
        if (message.contains("Data truncated for column")) {
            startIndex = message.indexOf("'") + 1;
            endIndex = message.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        if (message.contains("Column") && message.contains("cannot be null")) {
            startIndex = message.indexOf("'") + 1;
            endIndex = message.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    private static String extractConstraintName(String message) {
        int endIndex;
        int startIndex;
        if (message == null) {
            return null;
        }
        if (message.contains("for key '")) {
            startIndex = message.indexOf("for key '") + 9;
            endIndex = message.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        if (message.contains("constraint [")) {
            startIndex = message.indexOf("constraint [") + 12;
            endIndex = message.indexOf("]", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    private static String extractEntityName(String message, String constraintName) {
        String[] parts;
        if (constraintName == null) {
            return null;
        }
        if (constraintName.contains("_") && (parts = constraintName.split("_")).length > 1 && (parts[0].equalsIgnoreCase("uk") || parts[0].equalsIgnoreCase("pk") || parts[0].equalsIgnoreCase("fk"))) {
            return parts[1];
        }
        if (message.contains("for key '") && message.contains("entry '")) {
            int startIndex = message.indexOf("entry '") + 7;
            int endIndex = message.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    public static String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

