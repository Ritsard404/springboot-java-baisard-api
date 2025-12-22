/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 */
package com.ritsard.baisard.utils.handler;

import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.enums.ErrorCode;
import com.ritsard.baisard.utils.exceptions.crypto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

@RestControllerAdvice
public class CryptoExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CryptoExceptionHandler.class);

    @ExceptionHandler(value={EncryptionException.class})
    protected ResponseEntity<?> handleEncryptionException(EncryptionException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        String algorithm = ex.getAlgorithm() != null ? ex.getAlgorithm() : "Unknown";
        log.error("\n==================== \uc554\ud638\ud654 \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd10 \uc554\ud638\ud654 \uc815\ubcf4:\n   - \uc54c\uace0\ub9ac\uc998: {}\n   - \uc624\ub958 \uba54\uc2dc\uc9c0: {}\n   - \uc6d0\uc778: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), algorithm, ex.getMessage(), ex.getCause() != null ? ex.getCause().getMessage() : "\uc5c6\uc74c"});
        this.logJumpableStackTrace(ex);
        String userMessage = "\ub370\uc774\ud130 \uc554\ud638\ud654 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
        if (ex.getCause() instanceof InvalidKeyException) {
            userMessage = "\uc554\ud638\ud654 \ud0a4\uac00 \uc720\ud6a8\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
        } else if (ex.getCause() instanceof InvalidAlgorithmParameterException) {
            userMessage = "\uc554\ud638\ud654 \ud30c\ub77c\ubbf8\ud130\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
        } else if (ex.getMessage() != null) {
            if (ex.getMessage().contains("memory") || ex.getMessage().contains("Memory")) {
                userMessage = "\uc554\ud638\ud654 \ucc98\ub9ac\ub97c \uc704\ud55c \uba54\ubaa8\ub9ac\uac00 \ubd80\uc871\ud569\ub2c8\ub2e4. \uc7a0\uc2dc \ud6c4 \ub2e4\uc2dc \uc2dc\ub3c4\ud574\uc8fc\uc138\uc694.";
                return ResponseEntity.status((HttpStatusCode)HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.error(ErrorCode.CRYPTO_MEMORY_ERROR, userMessage));
            }
            if (ex.getMessage().contains("parameter") || ex.getMessage().contains("Parameter")) {
                userMessage = "\uc554\ud638\ud654 \ud30c\ub77c\ubbf8\ud130\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
                return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.CRYPTO_PARAMETER_ERROR, userMessage));
            }
        }
        ApiResponse response = ApiResponse.error(ErrorCode.ENCRYPTION_ERROR, userMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value={DecryptionException.class})
    protected ResponseEntity<?> handleDecryptionException(DecryptionException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        String algorithm = ex.getAlgorithm() != null ? ex.getAlgorithm() : "Unknown";
        log.error("\n==================== \ubcf5\ud638\ud654 \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd13 \ubcf5\ud638\ud654 \uc815\ubcf4:\n   - \uc54c\uace0\ub9ac\uc998: {}\n   - \uc624\ub958 \uba54\uc2dc\uc9c0: {}\n   - \uc6d0\uc778: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), algorithm, ex.getMessage(), ex.getCause() != null ? ex.getCause().getMessage() : "\uc5c6\uc74c"});
        this.logJumpableStackTrace(ex);
        String userMessage = "\ub370\uc774\ud130 \ubcf5\ud638\ud654 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
        if (ex.getCause() instanceof BadPaddingException) {
            userMessage = "\uc798\ubabb\ub41c \uc554\ud638\ud654 \ud0a4\uc774\uac70\ub098 \uc190\uc0c1\ub41c \ub370\uc774\ud130\uc785\ub2c8\ub2e4.";
        } else if (ex.getCause() instanceof IllegalBlockSizeException) {
            userMessage = "\uc554\ud638\ud654\ub41c \ub370\uc774\ud130\uc758 \ud615\uc2dd\uc774 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
        } else if (ex.getMessage() != null) {
            if (ex.getMessage().contains("wrong key") || ex.getMessage().contains("Wrong key")) {
                userMessage = "\ubcf5\ud638\ud654 \ud0a4\uac00 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
            } else if (ex.getMessage().contains("version") || ex.getMessage().contains("Version")) {
                userMessage = "\uc554\ud638\ud654 \ubc84\uc804\uc774 \ud638\ud658\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.";
                return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.CRYPTO_VERSION_ERROR, userMessage));
            }
        }
        ApiResponse response = ApiResponse.error(ErrorCode.DECRYPTION_ERROR, userMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value={CryptoKeyException.class})
    protected ResponseEntity<?> handleCryptoKeyException(CryptoKeyException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\n==================== \uc554\ud638\ud654 \ud0a4 \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd11 \ud0a4 \uc624\ub958:\n   - \ud0a4 \ud0c0\uc785: {}\n   - \uc624\ub958 \uba54\uc2dc\uc9c0: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), ex.getKeyType() != null ? ex.getKeyType() : "Unknown", ex.getMessage()});
        this.logJumpableStackTrace(ex);
        String userMessage = "\uc554\ud638\ud654 \ud0a4 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
        if ("PUBLIC_KEY".equals(ex.getKeyType())) {
            userMessage = "\uacf5\uac1c\ud0a4 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
        } else if ("PRIVATE_KEY".equals(ex.getKeyType())) {
            userMessage = "\uac1c\uc778\ud0a4 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
        } else if ("SECRET_KEY".equals(ex.getKeyType())) {
            userMessage = "\ube44\ubc00\ud0a4 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.";
        }
        ApiResponse response = ApiResponse.error(ErrorCode.CRYPTO_KEY_ERROR, userMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value={PasswordHashingException.class})
    protected ResponseEntity<?> handlePasswordHashingException(PasswordHashingException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\n==================== \ud328\uc2a4\uc6cc\ub4dc \ud574\uc2f1 \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd10 \ud574\uc2f1 \uc815\ubcf4:\n   - \uc54c\uace0\ub9ac\uc998: {}\n   - \uc624\ub958 \uba54\uc2dc\uc9c0: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), ex.getAlgorithm() != null ? ex.getAlgorithm() : "Unknown", ex.getMessage()});
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.PASSWORD_HASHING_ERROR, "\ud328\uc2a4\uc6cc\ub4dc \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value={PasswordVerificationException.class})
    protected ResponseEntity<?> handlePasswordVerificationException(PasswordVerificationException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\n==================== \ud328\uc2a4\uc6cc\ub4dc \uac80\uc99d \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd13 \uac80\uc99d \uc624\ub958:\n   - \uc624\ub958 \uba54\uc2dc\uc9c0: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), ex.getMessage()});
        ApiResponse response = ApiResponse.error(ErrorCode.PASSWORD_VERIFICATION_ERROR, "\ud328\uc2a4\uc6cc\ub4dc \uac80\uc99d \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value={InvalidKeyException.class})
    protected ResponseEntity<?> handleInvalidKeyException(InvalidKeyException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\ud83d\udd11 [\uc798\ubabb\ub41c \ud0a4 \uc624\ub958] [ID:{}]: {}", (Object)errorId, (Object)ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.INVALID_KEY_ERROR, "\uc554\ud638\ud654 \ud0a4\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value={InvalidKeySpecException.class})
    protected ResponseEntity<?> handleInvalidKeySpecException(InvalidKeySpecException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\ud83d\udd11 [\uc798\ubabb\ub41c \ud0a4 \uc2a4\ud399 \uc624\ub958] [ID:{}]: {}", (Object)errorId, (Object)ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.INVALID_KEY_SPEC_ERROR, "\uc554\ud638\ud654 \ud0a4 \ud615\uc2dd\uc774 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value={NoSuchAlgorithmException.class})
    protected ResponseEntity<?> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\ud83d\udd10 [\uc9c0\uc6d0\ud558\uc9c0 \uc54a\ub294 \uc54c\uace0\ub9ac\uc998 \uc624\ub958] [ID:{}]: {}", (Object)errorId, (Object)ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.UNSUPPORTED_ALGORITHM_ERROR, "\uc9c0\uc6d0\ud558\uc9c0 \uc54a\ub294 \uc554\ud638\ud654 \uc54c\uace0\ub9ac\uc998\uc785\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value={BadPaddingException.class})
    protected ResponseEntity<?> handleBadPaddingException(BadPaddingException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\ud83d\udd13 [\ud328\ub529 \uc624\ub958] [ID:{}]: {}", (Object)errorId, (Object)ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.BAD_PADDING_ERROR, "\uc554\ud638\ud654\ub41c \ub370\uc774\ud130\uac00 \uc190\uc0c1\ub418\uc5c8\uac70\ub098 \ud0a4\uac00 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value={IllegalBlockSizeException.class})
    protected ResponseEntity<?> handleIllegalBlockSizeException(IllegalBlockSizeException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\ud83d\udccf [\ube14\ub85d \ud06c\uae30 \uc624\ub958] [ID:{}]: {}", (Object)errorId, (Object)ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.ILLEGAL_BLOCK_SIZE_ERROR, "\uc554\ud638\ud654 \ub370\uc774\ud130\uc758 \ud06c\uae30\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value={InvalidAlgorithmParameterException.class})
    protected ResponseEntity<?> handleInvalidAlgorithmParameterException(InvalidAlgorithmParameterException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        log.error("\u2699\ufe0f [\uc54c\uace0\ub9ac\uc998 \ud30c\ub77c\ubbf8\ud130 \uc624\ub958] [ID:{}]: {}", (Object)errorId, (Object)ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.INVALID_ALGORITHM_PARAMETER_ERROR, "\uc554\ud638\ud654 \uc54c\uace0\ub9ac\uc998 \ud30c\ub77c\ubbf8\ud130\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.");
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.BAD_REQUEST).body(response);
    }

    private String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void logJumpableStackTrace(Throwable ex) {
        if (ex == null || ex.getStackTrace() == null) {
            return;
        }
        StringBuilder stackLog = new StringBuilder("\n\ud83d\udccd \uc2a4\ud0dd \ud2b8\ub808\uc774\uc2a4:");
        int count = 0;
        for (StackTraceElement element : ex.getStackTrace()) {
            String className = element.getClassName();
            if (!className.startsWith("com.lodong") || count >= 5) continue;
            stackLog.append(String.format("\n    %s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()));
            ++count;
        }
        if (count == 0) {
            for (int i = 0; i < Math.min(3, ex.getStackTrace().length); ++i) {
                StackTraceElement element = ex.getStackTrace()[i];
                stackLog.append(String.format("\n    %s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()));
            }
        }
        log.error(stackLog.toString());
    }
}

