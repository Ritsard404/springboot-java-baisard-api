/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParseException
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  jakarta.persistence.EntityExistsException
 *  jakarta.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.DuplicateKeyException
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.orm.jpa.JpaSystemException
 *  org.springframework.security.access.AccessDeniedException
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.interceptor.TransactionAspectSupport
 *  org.springframework.validation.FieldError
 *  org.springframework.web.HttpMediaTypeNotSupportedException
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.MissingRequestHeaderException
 *  org.springframework.web.bind.MissingServletRequestParameterException
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 *  org.springframework.web.client.HttpClientErrorException
 *  org.springframework.web.client.HttpClientErrorException$BadRequest
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
 *  org.springframework.web.servlet.NoHandlerFoundException
 */
package com.ritsard.baisard.utils.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.enums.ErrorCode;
import com.ritsard.baisard.utils.exceptions.*;
import com.ritsard.baisard.utils.exceptions.RuntimeException;
import com.ritsard.baisard.utils.exceptions.files.InvalidFileTypeException;
import com.ritsard.baisard.utils.exceptions.gpt.ContentFilteringException;
import com.ritsard.baisard.utils.exceptions.gpt.GptException;
import com.ritsard.baisard.utils.exceptions.gpt.RateLimitException;
import com.ritsard.baisard.utils.exceptions.gpt.TokenLimitExceededException;
import com.ritsard.baisard.utils.logging.DbExceptionUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    private static final String APP_PACKAGE = "com.ritsardF";
    private static final int MAX_STACK_LINES = 10;

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n==================== \ud0c0\uc785 \ubd88\uc77c\uce58 \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n");
        logMessage.append(String.format("\ud83c\udd94 \uc624\ub958 ID: %s\n", errorId));
        logMessage.append("\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n");
        logMessage.append(String.format("   - URL: %s %s\n", request.getMethod(), request.getRequestURI()));
        logMessage.append(String.format("   - \uc694\uccad \ud30c\ub77c\ubbf8\ud130: %s\n", this.formatRequestParams(request.getParameterMap())));
        logMessage.append(String.format("   - \ucffc\ub9ac\uc2a4\ud2b8\ub9c1: %s\n", request.getQueryString()));
        logMessage.append(String.format("   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: %s\n", request.getRemoteAddr()));
        logMessage.append(String.format("   - User-Agent: %s\n", request.getHeader("User-Agent")));
        logMessage.append("\ud83d\udd34 \uc624\ub958 \uc0c1\uc138:\n");
        logMessage.append(String.format("   - \ud30c\ub77c\ubbf8\ud130\uba85: %s\n", ex.getName()));
        logMessage.append(String.format("   - \uc798\ubabb\ub41c \uac12: '%s'\n", ex.getValue()));
        logMessage.append(String.format("   - \uc608\uc0c1 \ud0c0\uc785: %s\n", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown"));
        logMessage.append(String.format("   - \uc2e4\uc81c \ud0c0\uc785: %s\n", ex.getValue() != null ? ex.getValue().getClass().getSimpleName() : "null"));
        logMessage.append(String.format("   - \uc624\ub958 \uba54\uc2dc\uc9c0: %s\n", ex.getMessage()));
        Throwable rootCause = this.getRootCause((Throwable) ex);
        if (rootCause != ex && rootCause.getMessage() != null) {
            logMessage.append(String.format("\ud83d\udca5 \uadfc\ubcf8 \uc6d0\uc778: %s - %s\n", rootCause.getClass().getSimpleName(), rootCause.getMessage()));
        }
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Class enumType = ex.getRequiredType();
            Object[] enumConstants = enumType.getEnumConstants();
            String validValues = Arrays.stream(enumConstants).map(Object::toString).collect(Collectors.joining(", "));
            logMessage.append(String.format("\ud83d\udccb \uc720\ud6a8\ud55c \uac12 \ubaa9\ub85d: [%s]\n", validValues));
            log.error(logMessage.toString());
            this.logJumpableStackTrace((Throwable) ex);
            String userMessage = String.format("'%s' \ud30c\ub77c\ubbf8\ud130\uc5d0 \uc798\ubabb\ub41c \uac12 '%s'\uc774(\uac00) \uc785\ub825\ub418\uc5c8\uc2b5\ub2c8\ub2e4. \uc720\ud6a8\ud55c \uac12: [%s]", ex.getName(), ex.getValue(), validValues);
            ApiResponse response = ApiResponse.error(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_ERROR, userMessage);
            response.setErrorId(errorId);
            return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(response);
        }
        log.error(logMessage.toString());
        this.logJumpableStackTrace((Throwable) ex);
        String userMessage = String.format("'%s' \ud30c\ub77c\ubbf8\ud130\uc5d0 \uc798\ubabb\ub41c \ud0c0\uc785\uc758 \uac12\uc774 \uc785\ub825\ub418\uc5c8\uc2b5\ub2c8\ub2e4. (\uc785\ub825\uac12: '%s', \uc608\uc0c1 \ud0c0\uc785: %s)", ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
        ApiResponse response = ApiResponse.error(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_ERROR, userMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(response);
    }

    private String formatRequestParams(Map<String, String[]> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return "\uc5c6\uc74c";
        }
        return parameterMap.entrySet().stream().map(entry -> {
            String key = (String) entry.getKey();
            String value = String.join((CharSequence) ", ", (CharSequence[]) entry.getValue());
            if (this.isSensitiveParam(key)) {
                return key + "=[MASKED]";
            }
            return key + "=" + value;
        }).collect(Collectors.joining(", "));
    }

    @ExceptionHandler(value = {JpaSystemException.class})
    protected ResponseEntity<?> handleJpaSystemException(JpaSystemException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        Throwable rootCause = this.getRootCause((Throwable) ex);
        String errorMessage = rootCause.getMessage();
        String customMessage = "\ub370\uc774\ud130\ubca0\uc774\uc2a4 \uc791\uc5c5 \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4";
        ErrorCode errorCode = ErrorCode.DATABASE_ERROR;
        log.error("\n==================== JPA \uc2dc\uc2a4\ud15c \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \uc694\uccad \ud30c\ub77c\ubbf8\ud130: {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd34 \uc624\ub958 \uba54\uc2dc\uc9c0: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), this.formatRequestParams(request.getParameterMap()), request.getRemoteAddr(), errorMessage});
        if (errorMessage.contains("Data truncated for column")) {
            String columnName = this.extractColumnName(errorMessage);
            if (columnName != null) {
                String[] values;
                customMessage = String.format("\uceec\ub7fc '%s'\uc5d0 \ub108\ubb34 \uae34 \ub370\uc774\ud130\uac00 \uc785\ub825\ub418\uc5c8\uc2b5\ub2c8\ub2e4. \ub370\uc774\ud130 \uae38\uc774\ub97c \ud655\uc778\ud558\uc138\uc694.", columnName);
                Map params = request.getParameterMap();
                if (params.containsKey(columnName) && (values = (String[]) params.get(columnName)) != null && values.length > 0) {
                    log.error("\ud83d\udd0d \ubb38\uc81c \uceec\ub7fc '{}' \uac12: '{}' (\uae38\uc774: {})", new Object[]{columnName, values[0], values[0].length()});
                }
            }
            errorCode = ErrorCode.DATA_TRUNCATION_ERROR;
        }
        this.collectExecutionContext((Exception) ex, errorId);
        this.logJumpableStackTrace((Throwable) ex);
        ApiResponse response = ApiResponse.error(errorCode, customMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(response);
    }

    private void collectExecutionContext(Exception ex, String errorId) {
        try {
            TransactionStatus txStatus = TransactionAspectSupport.currentTransactionStatus();
            if (txStatus != null) {
                log.error("\ud83d\udd04 [\ud2b8\ub79c\uc7ad\uc158 \uc815\ubcf4] \uc0c8 \ud2b8\ub79c\uc7ad\uc158?: {}, \ub864\ubc31 \uc804\uc6a9?: {}, \uc644\ub8cc\ub428?: {}", new Object[]{txStatus.isNewTransaction(), txStatus.isRollbackOnly(), txStatus.isCompleted()});
            }
        } catch (Exception e) {
            log.debug("\ud2b8\ub79c\uc7ad\uc158 \uc815\ubcf4 \uc218\uc9d1 \uc2e4\ud328: {}", (Object) e.getMessage());
        }
        StringBuilder entityInfo = new StringBuilder();
        Map<String, String> componentInfo = this.findImportantComponents(ex.getStackTrace());
        if (componentInfo.containsKey("controller")) {
            entityInfo.append("\n  - \ud83c\udfae Controller: ").append(componentInfo.get("controller"));
        }
        if (componentInfo.containsKey("service")) {
            entityInfo.append("\n  - \ud83d\udd27 Service: ").append(componentInfo.get("service"));
        }
        if (componentInfo.containsKey("repository")) {
            entityInfo.append("\n  - \ud83d\udcbe Repository: ").append(componentInfo.get("repository"));
        }
        if (entityInfo.length() > 0) {
            log.error("\ud83d\udccd [\uad00\ub828 \uc560\ud50c\ub9ac\ucf00\uc774\uc158 \ucef4\ud3ec\ub10c\ud2b8] \uc624\ub958 ID: {}, \ucef4\ud3ec\ub10c\ud2b8: {}", (Object) errorId, (Object) entityInfo);
        }
    }

    private Map<String, String> findImportantComponents(StackTraceElement[] stackTrace) {
        HashMap<String, String> components = new HashMap<String, String>();
        if (stackTrace == null || stackTrace.length == 0) {
            return components;
        }
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (!className.startsWith(APP_PACKAGE)) continue;
            String formattedLocation = String.format("%s.%s(%s:%d)", className, element.getMethodName(), element.getFileName(), element.getLineNumber());
            if (className.contains("Controller") && !components.containsKey("controller")) {
                components.put("controller", formattedLocation);
                continue;
            }
            if (className.contains("Service") && !components.containsKey("service")) {
                components.put("service", formattedLocation);
                continue;
            }
            if (!className.contains("Repository") || components.containsKey("repository")) continue;
            components.put("repository", formattedLocation);
        }
        return components;
    }

    private String extractColumnName(String message) {
        if (message == null) {
            return null;
        }
        if (message.contains("Data truncated for column")) {
            int startIndex = message.indexOf("'") + 1;
            int endIndex = message.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        int maxDepth = 10;
        for (int depth = 0; cause.getCause() != null && cause != cause.getCause() && depth < maxDepth; cause = cause.getCause(), ++depth) {
        }
        return cause;
    }

    @ExceptionHandler(value = {InvalidFileTypeException.class})
    public ResponseEntity<?> handleInvalidFileTypeException(InvalidFileTypeException ex) {
        return this.buildErrorResponse(ex, ErrorCode.INVALID_FILE_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }

    @ExceptionHandler(value = {SQLException.class})
    protected ResponseEntity<?> handleSQLException(SQLException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        String customMessage = "\ub370\uc774\ud130\ubca0\uc774\uc2a4 \uc791\uc5c5 \uc911 SQL \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4";
        ErrorCode errorCode = ErrorCode.SQL_ERROR;
        int sqlErrorCode = ex.getErrorCode();
        String sqlState = ex.getSQLState();
        log.error("\n==================== SQL \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n\ud83d\udd34 SQL \uc624\ub958:\n   - \uc624\ub958 \ucf54\ub4dc: {}\n   - SQL \uc0c1\ud0dc: {}\n   - \uba54\uc2dc\uc9c0: {}", new Object[]{errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), sqlErrorCode, sqlState, ex.getMessage()});
        if (sqlErrorCode == 1406) {
            customMessage = "\ub370\uc774\ud130 \uae38\uc774\uac00 \ucd5c\ub300 \ud5c8\uc6a9 \uae38\uc774\ub97c \ucd08\uacfc\ud588\uc2b5\ub2c8\ub2e4. \uc785\ub825 \ub370\uc774\ud130\ub97c \ud655\uc778\ud558\uc138\uc694.";
            errorCode = ErrorCode.DATA_TRUNCATION_ERROR;
        } else if (sqlErrorCode == 1062) {
            customMessage = "\uc911\ubcf5\ub41c \ub370\uc774\ud130\uac00 \uc874\uc7ac\ud569\ub2c8\ub2e4. \uace0\uc720\ud55c \uac12\uc744 \uc785\ub825\ud558\uc138\uc694.";
            errorCode = ErrorCode.UNIQUE_CONSTRAINT_ERROR;
        } else if (sqlErrorCode == 1452) {
            customMessage = "\ucc38\uc870 \ubb34\uacb0\uc131 \uc81c\uc57d\uc870\uac74 \uc704\ubc18. \ucc38\uc870\ud558\ub294 \ub370\uc774\ud130\uac00 \uc874\uc7ac\ud558\ub294\uc9c0 \ud655\uc778\ud558\uc138\uc694.";
            errorCode = ErrorCode.FOREIGN_KEY_VIOLATION_ERROR;
        }
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(errorCode, customMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.UNAUTHORIZED_ERROR, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        log.error("\u26a0\ufe0f [\uc798\ubabb\ub41c \uc778\uc790 \uc624\ub958] [ID:{}]: {}", (Object) errorId, (Object) ex.getMessage());
        this.logJumpableStackTrace(ex);
        ApiResponse response = ApiResponse.error(ErrorCode.BAD_REQUEST_ERROR, ex.getMessage());
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ResponseEntity<?> buildErrorResponse(Exception ex, ErrorCode errorCode, HttpStatus status, String customMessage) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        try {
            HttpServletRequest request = null;
            try {
                ServletWebRequest attrs = (ServletWebRequest) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    request = attrs.getRequest();
                }
            } catch (Exception e) {
                log.debug("\uc694\uccad \uc815\ubcf4 \ud68d\ub4dd \uc2e4\ud328: {}", (Object) e.getMessage());
            }
            if (request != null) {
                String contentType;
                Map params;
                StringBuilder logMessage = new StringBuilder();
                logMessage.append("\n==================== \uc624\ub958 \uc0c1\uc138 \uc815\ubcf4 ====================\n");
                logMessage.append(String.format("\ud83c\udd94 \uc624\ub958 ID: %s\n", errorId));
                logMessage.append("\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n");
                logMessage.append(String.format("   - URL: %s %s\n", request.getMethod(), request.getRequestURI()));
                if (request.getQueryString() != null) {
                    logMessage.append(String.format("   - \ucffc\ub9ac\uc2a4\ud2b8\ub9c1: %s\n", request.getQueryString()));
                }
                if (!(params = request.getParameterMap()).isEmpty()) {
                    logMessage.append("   - \ud30c\ub77c\ubbf8\ud130:\n");
                    params.forEach((key, values) -> {
                        String value = String.join((CharSequence) ", ", values.toString());
                        if (!this.isSensitiveParam((String) key)) {
                            logMessage.append(String.format("      * %s: %s\n", key, value));
                        } else {
                            logMessage.append(String.format("      * %s: [MASKED]\n", key));
                        }
                    });
                }
                if ((contentType = request.getContentType()) != null) {
                    logMessage.append(String.format("   - Content-Type: %s\n", contentType));
                }
                logMessage.append(String.format("   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: %s\n", request.getRemoteAddr()));
                logMessage.append(String.format("   - User-Agent: %s\n", request.getHeader("User-Agent")));
                logMessage.append("\ud83d\udd34 \uc624\ub958:\n");
                logMessage.append(String.format("   - \ud0c0\uc785: %s\n", ex.getClass().getSimpleName()));
                logMessage.append(String.format("   - \uba54\uc2dc\uc9c0: %s\n", customMessage));
                if (ex.getMessage() != null && !ex.getMessage().equals(customMessage)) {
                    logMessage.append(String.format("   - \uc6d0\ubcf8 \uba54\uc2dc\uc9c0: %s\n", ex.getMessage()));
                }
                log.error(logMessage.toString());
            } else {
                log.error("\u26a0\ufe0f [{}] \uc5d0\ub7ec \ubc1c\uc0dd [ID:{}]: {}", new Object[]{ex.getClass().getSimpleName(), errorId, customMessage});
            }
            this.logJumpableStackTrace(ex);
            Throwable rootCause = this.getRootCause(ex);
            if (rootCause != ex) {
                log.error("\ud83d\udca5 \uadfc\ubcf8 \uc6d0\uc778: {} - {}", (Object) rootCause.getClass().getSimpleName(), (Object) rootCause.getMessage());
                if (!rootCause.getClass().equals(ex.getClass())) {
                    this.logJumpableStackTrace(rootCause);
                }
            }
            ApiResponse response = ApiResponse.error(errorCode, customMessage);
            response.setErrorId(errorId);
            ResponseEntity responseEntity = ResponseEntity.status((HttpStatusCode) status).body(response);
            return responseEntity;
        } finally {
            MDC.remove((String) "errorId");
        }
    }

    private void logJumpableStackTrace(Throwable ex) {
        if (ex == null || ex.getStackTrace() == null) {
            return;
        }
        ArrayList<String> controllerFrames = new ArrayList<String>();
        ArrayList<String> serviceFrames = new ArrayList<String>();
        ArrayList<String> repositoryFrames = new ArrayList<String>();
        ArrayList<String> otherAppFrames = new ArrayList<String>();
        for (StackTraceElement element : ex.getStackTrace()) {
            String className = element.getClassName();
            if (!className.startsWith(APP_PACKAGE)) continue;
            String frame2 = String.format("%s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
            if (className.contains("Controller")) {
                controllerFrames.add(frame2);
                continue;
            }
            if (className.contains("Service")) {
                serviceFrames.add(frame2);
                continue;
            }
            if (className.contains("Repository")) {
                repositoryFrames.add(frame2);
                continue;
            }
            otherAppFrames.add(frame2);
        }
        StringBuilder stackLog = new StringBuilder("\n\ud83d\udccd \uc2a4\ud0dd \ud2b8\ub808\uc774\uc2a4 \ubd84\uc11d:");
        if (!controllerFrames.isEmpty()) {
            stackLog.append("\n\ud83c\udfae Controller:");
            controllerFrames.forEach(frame -> stackLog.append("\n    ").append((String) frame));
        }
        if (!serviceFrames.isEmpty()) {
            stackLog.append("\n\ud83d\udd27 Service:");
            serviceFrames.forEach(frame -> stackLog.append("\n    ").append((String) frame));
        }
        if (!repositoryFrames.isEmpty()) {
            stackLog.append("\n\ud83d\udcbe Repository:");
            repositoryFrames.forEach(frame -> stackLog.append("\n    ").append((String) frame));
        }
        if (!otherAppFrames.isEmpty()) {
            stackLog.append("\n\ud83d\udce6 \uae30\ud0c0 \uc560\ud50c\ub9ac\ucf00\uc774\uc158 \ucf54\ub4dc:");
            otherAppFrames.stream().limit(5L).forEach(frame -> stackLog.append("\n    ").append((String) frame));
        }
        if (controllerFrames.isEmpty() && serviceFrames.isEmpty() && repositoryFrames.isEmpty() && otherAppFrames.isEmpty()) {
            stackLog.append("\n\u26a0\ufe0f \uc560\ud50c\ub9ac\ucf00\uc774\uc158 \ucf54\ub4dc\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. \uc804\uccb4 \uc2a4\ud0dd (\ucc98\uc74c 3\uac1c):");
            int count = 0;
            for (StackTraceElement element : ex.getStackTrace()) {
                if (count++ >= 3) continue;
                stackLog.append(String.format("\n    %s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()));
            }
        }
        log.error(stackLog.toString());
    }

    private void logRequestInfo() {
        try {
            ServletWebRequest attrs = (ServletWebRequest) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                log.error("\ud83c\udf10 \uc694\uccad \uc815\ubcf4: {} {}", (Object) request.getMethod(), (Object) request.getRequestURI());
                if (request.getQueryString() != null) {
                    log.error("\ud83d\udd0d \ucffc\ub9ac \uc2a4\ud2b8\ub9c1: {}", (Object) request.getQueryString());
                }
                HashMap params = new HashMap();
                request.getParameterMap().forEach((key, values) -> {
                    String value = String.join((CharSequence) ", ", values);
                    if (this.isSensitiveParam((String) key)) {
                        params.put(key, "********");
                    } else {
                        params.put(key, value);
                    }
                });
                if (!params.isEmpty()) {
                    log.error("\ud83d\udcdd \uc694\uccad \ud30c\ub77c\ubbf8\ud130: {}", params);
                }
            }
        } catch (Exception e) {
            log.debug("\uc694\uccad \uc815\ubcf4 \ub85c\uae45 \uc2e4\ud328: {}", (Object) e.getMessage());
        }
    }

    private void logRequestObject(Object requestObject) {
        if (requestObject == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(requestObject);
            log.error("\ud83d\udce6 \uc694\uccad \uac1d\uccb4 \uad6c\uc870:\n{}", (Object) json);
        } catch (Exception e) {
            log.error("\uc694\uccad \uac1d\uccb4 \uc9c1\ub82c\ud654 \uc2e4\ud328: {}", (Object) e.getMessage());
        }
    }

    private boolean isSensitiveParam(String paramName) {
        if (paramName == null) {
            return false;
        }
        String lowerName = paramName.toLowerCase();
        return lowerName.contains("password") || lowerName.contains("token") || lowerName.contains("key") || lowerName.contains("secret") || lowerName.contains("credential") || lowerName.contains("auth");
    }

    private Map<String, Object> collectErrorDetails(Exception ex, String errorType) {
        HashMap<String, Object> details = new HashMap<String, Object>();
        details.put("exceptionType", errorType);
        details.put("exceptionClass", ex.getClass().getName());
        details.put("message", ex.getMessage());
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement relevantFrame = this.findRelevantStackFrame(stackTrace);
            details.put("className", relevantFrame.getClassName());
            details.put("methodName", relevantFrame.getMethodName());
            details.put("fileName", relevantFrame.getFileName());
            details.put("lineNumber", relevantFrame.getLineNumber());
        }
        try {
            String requestId;
            HttpServletRequest request = ((ServletWebRequest) RequestContextHolder.getRequestAttributes()).getRequest();
            details.put("requestUri", request.getRequestURI());
            details.put("requestMethod", request.getMethod());
            details.put("queryString", request.getQueryString());
            details.put("remoteAddr", request.getRemoteAddr());
            details.put("userAgent", request.getHeader("User-Agent"));
            Object userId = request.getAttribute("userId");
            if (userId != null) {
                MDC.put((String) "userId", (String) userId.toString());
                details.put("userId", userId);
            }
            if ((requestId = (String) request.getAttribute("requestId")) != null && !requestId.isEmpty()) {
                MDC.put((String) "requestId", (String) requestId);
                details.put("requestId", requestId);
            }
        } catch (Exception e) {
            details.put("requestUri", "\uc694\uccad \uc815\ubcf4 \uc5c6\uc74c");
            details.put("requestMethod", "UNKNOWN");
        }
        return details;
    }

    private StackTraceElement findRelevantStackFrame(StackTraceElement[] stackTrace) {
        for (StackTraceElement element : stackTrace) {
            if (!element.getClassName().startsWith(APP_PACKAGE)) continue;
            return element;
        }
        for (StackTraceElement element : stackTrace) {
            if (!element.getClassName().contains("Controller") && !element.getClassName().contains("Service")) continue;
            return element;
        }
        return stackTrace[0];
    }

    private String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @ExceptionHandler(value = {ProcessorException.class})
    public ResponseEntity<?> handleProcessorException(ProcessorException ex, WebRequest request) {
        String message = String.format("%s (\ud504\ub85c\uc138\uc11c: %s)", ex.getMessage(), ex.getProcessorName());
        return this.buildErrorResponse(ex, ErrorCode.PROCESSOR_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(value = {GptException.class})
    public ResponseEntity<?> handleGptException(GptException ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.GPT_UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = {ApiKeyException.class})
    public ResponseEntity<?> handleApiKeyException(ApiKeyException ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.INVALID_API_KEY, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {RateLimitException.class})
    public ResponseEntity<?> handleRateLimitException(RateLimitException ex, WebRequest request) {
        String message = String.format("%s (\uc57d %d\ucd08 \ud6c4 \uc7ac\uc2dc\ub3c4 \uac00\ub2a5)", ex.getMessage(), ex.getRetryAfterSeconds());
        ResponseEntity<?> response = this.buildErrorResponse(ex, ErrorCode.RATE_LIMIT_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS, message);
        return ((ResponseEntity.BodyBuilder) ResponseEntity.status((HttpStatusCode) HttpStatus.TOO_MANY_REQUESTS).header("Retry-After", new String[]{String.valueOf(ex.getRetryAfterSeconds())})).body(response.getBody());
    }

    @ExceptionHandler(value = {TokenLimitExceededException.class})
    public ResponseEntity<?> handleTokenLimitExceededException(TokenLimitExceededException ex, WebRequest request) {
        String message = String.format("%s (\ud604\uc7ac: %d, \ucd5c\ub300: %d)", ex.getMessage(), ex.getCurrentTokenCount(), ex.getMaxTokens());
        return this.buildErrorResponse(ex, ErrorCode.TOKEN_LIMIT_EXCEEDED, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {ContentFilteringException.class})
    public ResponseEntity<?> handleContentFilteringException(ContentFilteringException ex, WebRequest request) {
        String message = String.format("%s (\ud544\ud130\ub9c1 \uce74\ud14c\uace0\ub9ac: %s)", ex.getMessage(), ex.getFilteredCategory());
        return this.buildErrorResponse(ex, ErrorCode.CONTENT_FILTERED, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {TimeoutException.class, SocketTimeoutException.class})
    public ResponseEntity<?> handleTimeoutException(Exception ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.API_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT, "\uc694\uccad \uc2dc\uac04\uc774 \ucd08\uacfc\ub418\uc5c8\uc2b5\ub2c8\ub2e4.");
    }

    @ExceptionHandler(value = {ServerErrorException.class})
    public ResponseEntity<?> handleServerErrorException(ServerErrorException ex, WebRequest request) {
        ErrorCode errorCode = ex.getStatusCode() == 502 ? ErrorCode.BAD_GATEWAY : (ex.getStatusCode() == 503 ? ErrorCode.SERVICE_UNAVAILABLE : ErrorCode.API_SERVER_ERROR);
        return this.buildErrorResponse(ex, errorCode, HttpStatus.valueOf((int) ex.getStatusCode()), ex.getMessage());
    }

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<?> handleRequestException(RequestException ex, WebRequest request) {
        ErrorCode errorCode = ex.isUnauthorized() ? ErrorCode.API_UNAUTHORIZED : (ex.isForbidden() ? ErrorCode.API_FORBIDDEN : (ex.isNotFound() ? ErrorCode.RESOURCE_NOT_FOUND : (ex.isRateLimited() ? ErrorCode.RATE_LIMIT_EXCEEDED : ErrorCode.API_BAD_REQUEST)));
        return this.buildErrorResponse(ex, errorCode, HttpStatus.valueOf((int) ex.getStatusCode()), ex.getMessage());
    }

    @ExceptionHandler(value = {NetworkException.class})
    public ResponseEntity<?> handleNetworkException(NetworkException ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.NETWORK_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        String errorMessage = ex.getBindingResult().getFieldErrors().stream().map(this::formatFieldError).distinct().collect(Collectors.joining(" | "));
        log.error("\u26a0\ufe0f [\uac80\uc99d \uc624\ub958] [ID:{}]: {}", (Object) errorId, (Object) errorMessage);
        this.logJumpableStackTrace((Throwable) ex);
        ApiResponse response = ApiResponse.error(ErrorCode.METHOD_ARGUMENT_NOT_VALID_ERROR, errorMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.BAD_REQUEST).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        String field = fieldError.getField();
        String defaultMessage = fieldError.getDefaultMessage();
        Object rejectedValue = fieldError.getRejectedValue();
        if (this.isSensitiveParam(field)) {
            return String.format("\ud544\ub4dc '%s': %s", field, defaultMessage);
        }
        return String.format("\ud544\ub4dc '%s': %s (\uc785\ub825\uac12: '%s')", field, defaultMessage, rejectedValue);
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    protected ResponseEntity<?> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String message = String.format("\ud544\uc218 \ud5e4\ub354\uac00 \ub204\ub77d\ub418\uc5c8\uc2b5\ub2c8\ub2e4: %s", ex.getHeaderName());
        return this.buildErrorResponse((Exception) ex, ErrorCode.NOT_VALID_HEADER_ERROR, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "\uc694\uccad \ubcf8\ubb38\uc744 \uc77d\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. \uc720\ud6a8\ud55c JSON \ud615\uc2dd\uc778\uc9c0 \ud655\uc778\ud558\uc138\uc694.";
        return this.buildErrorResponse((Exception) ex, ErrorCode.REQUEST_BODY_MISSING_ERROR, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    protected ResponseEntity<?> handleMissingRequestHeaderExceptionException(MissingServletRequestParameterException ex) {
        String message = String.format("\ud544\uc218 \ud30c\ub77c\ubbf8\ud130\uac00 \ub204\ub77d\ub418\uc5c8\uc2b5\ub2c8\ub2e4: %s (%s \ud0c0\uc785)", ex.getParameterName(), ex.getParameterType());
        return this.buildErrorResponse((Exception) ex, ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {HttpClientErrorException.BadRequest.class})
    protected ResponseEntity<?> handleBadRequestException(HttpClientErrorException e) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String errorLocation = "\uc54c \uc218 \uc5c6\ub294 \uc704\uce58";
        if (stackTraceElements.length > 0) {
            StackTraceElement element = stackTraceElements[0];
            errorLocation = String.format("%s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
        }
        String message = String.format("%s (\uc704\uce58: %s)", e.getMessage(), errorLocation);
        return this.buildErrorResponse((Exception) e, ErrorCode.BAD_REQUEST_ERROR, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return this.buildErrorResponse((Exception) ex, ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN, "\uc811\uadfc \uad8c\ud55c\uc774 \uc5c6\uc2b5\ub2c8\ub2e4. \ud544\uc694\ud55c \uad8c\ud55c\uc744 \ud655\uc778\ud558\uc138\uc694.");
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    protected ResponseEntity<?> handleNoHandlerFoundExceptionException(NoHandlerFoundException ex, HttpServletRequest request) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        log.error("\n==================== 404 Not Found \uc0c1\uc138 \uc815\ubcf4 ====================\n\ud83c\udd94 \uc624\ub958 ID: {}\n\ud83d\udccd \uc694\uccad \uc815\ubcf4:\n   - URL: {} {}\n   - \ud5e4\ub354: {}\n   - \ud074\ub77c\uc774\uc5b8\ud2b8 IP: {}\n   - User-Agent: {}\n\u274c \uc874\uc7ac\ud558\uc9c0 \uc54a\ub294 \uc5d4\ub4dc\ud3ec\uc778\ud2b8\uc785\ub2c8\ub2e4.", new Object[]{errorId, ex.getHttpMethod(), ex.getRequestURL(), Collections.list(request.getHeaderNames()), request.getRemoteAddr(), request.getHeader("User-Agent")});
        String message = String.format("\uc694\uccad\ud558\uc2e0 \ub9ac\uc18c\uc2a4\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: %s %s", ex.getHttpMethod(), ex.getRequestURL());
        ApiResponse response = ApiResponse.error(ErrorCode.NOT_FOUND_ERROR, message);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {InvalidTokenRequestException.class})
    protected ResponseEntity<?> handleInvalidTokenRequestException(InvalidTokenRequestException ex) {
        return this.buildErrorResponse(ex, ErrorCode.INVALID_TOKEN_ERROR, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {JwtTokenIsNotValid.class})
    public ResponseEntity<?> handleJwtTokenIsNotValid(JwtTokenIsNotValid ex) {
        return this.buildErrorResponse(ex, ErrorCode.JWT_TOKEN_NOT_VALID_ERROR, HttpStatus.UNAUTHORIZED, "\uc778\uc99d \ud1a0\ud070\uc774 \uc720\ud6a8\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \ub2e4\uc2dc \ub85c\uadf8\uc778\ud574 \uc8fc\uc138\uc694.");
    }

    @ExceptionHandler(value = {JwtTokenExpiredException.class})
    public ResponseEntity<?> handleJwtTokenExpiredException(JwtTokenExpiredException ex) {
        return this.buildErrorResponse(ex, ErrorCode.EXPIRED_TOKEN_ERROR, HttpStatus.UNAUTHORIZED, "\uc778\uc99d \ud1a0\ud070\uc774 \ub9cc\ub8cc\ub418\uc5c8\uc2b5\ub2c8\ub2e4. \ub2e4\uc2dc \ub85c\uadf8\uc778\ud574 \uc8fc\uc138\uc694.");
    }

    @ExceptionHandler(value = {AuthentificationException.class})
    public ResponseEntity<?> handleAuthentificationException(AuthentificationException ex) {
        return this.buildErrorResponse(ex, ErrorCode.AUTHENTICATION_ERROR, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {InvalidPasswordException.class})
    public ResponseEntity<?> handleInvalidPasswordException(InvalidPasswordException ex) {
        return this.buildErrorResponse((Exception) ((Object) ex), ErrorCode.INVALID_PASSWORD_ERROR, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {NoSuchUserException.class})
    public ResponseEntity<?> handleNoSuchUserException(NoSuchUserException ex) {
        return this.buildErrorResponse(ex, ErrorCode.NO_SUCH_USER_ERROR, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    protected ResponseEntity<?> handleEntityExistsException(EntityExistsException ex) {
        return this.buildErrorResponse((Exception) ex, ErrorCode.DUPLICATE_ERROR, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(value = {DuplicateValueException.class})
    public ResponseEntity<?> handleDuplicateValueException(DuplicateValueException ex) {
        return this.buildErrorResponse(ex, ErrorCode.DUPLICATE_ERROR, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    protected ResponseEntity<?> duplicatedKeyException(DuplicateKeyException ex) {
        return this.buildErrorResponse((Exception) ex, ErrorCode.DUPLICATE_ERROR, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(value = {IdentifierDuplicatedException.class})
    public ResponseEntity<?> handleIdentifierDuplicatedException(IdentifierDuplicatedException ex) {
        return this.buildErrorResponse(ex, ErrorCode.IDENTIFIER_DUPLICATED_ERROR, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    protected ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorId = this.generateErrorId();
        MDC.put((String) "errorId", (String) errorId);
        Object message = "\uc774\ubbf8 \uc0ac\uc6a9 \uc911\uc778 \uac12\uc774 \uc874\uc7ac\ud569\ub2c8\ub2e4.";
        ErrorCode errorCode = ErrorCode.DUPLICATE_ERROR;
        Throwable rootCause = this.getRootCause((Throwable) ex);
        String errorMessage = rootCause.getMessage();
        log.error("\ud83d\udcdb [\ub370\uc774\ud130 \ubb34\uacb0\uc131 \uc704\ubc18] \uc624\ub958 ID: {}, \uba54\uc2dc\uc9c0: {}", (Object) errorId, (Object) errorMessage);
        Map<String, Object> requestInfo = DbExceptionUtils.collectRequestInfo();
        if (!requestInfo.isEmpty()) {
            log.error("\ud83c\udf10 \uc694\uccad \uc815\ubcf4: {} {}", requestInfo.get("method"), requestInfo.get("uri"));
        }
        if (errorMessage.contains("uk_base_member_phone_is_deleted")) {
            message = "\uc774\ubbf8 \ub4f1\ub85d\ub41c \ud578\ub4dc\ud3f0 \ubc88\ud638 \uc785\ub2c8\ub2e4.";
        } else if (errorMessage.contains("uk_base_member_email_is_deleted")) {
            message = "\uc774\ubbf8 \ub4f1\ub85d\ub41c \uc774\uba54\uc77c \uc8fc\uc18c \uc785\ub2c8\ub2e4.";
        } else if (errorMessage.contains("uk_")) {
            String constraintName = errorMessage.contains("uk_") ? errorMessage.substring(errorMessage.indexOf("uk_")) : errorMessage;
            message = "\uc774\ubbf8 \ub4f1\ub85d\ub41c \uace0\uc720 \uc815\ubcf4\uc785\ub2c8\ub2e4: " + constraintName;
            errorCode = ErrorCode.UNIQUE_CONSTRAINT_ERROR;
        } else if (errorMessage.toLowerCase().contains("foreign key")) {
            message = "\ub2e4\ub978 \ub370\uc774\ud130\uc5d0\uc11c \ucc38\uc870\ud558\uace0 \uc788\uc5b4 \ucc98\ub9ac\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.";
            errorCode = ErrorCode.FOREIGN_KEY_VIOLATION_ERROR;
        } else if (errorMessage.contains("Data truncated")) {
            String columnName = DbExceptionUtils.extractColumnName(errorMessage);
            message = columnName != null ? String.format("\uceec\ub7fc '%s'\uc5d0 \ub108\ubb34 \uae34 \ub370\uc774\ud130\uac00 \uc785\ub825\ub418\uc5c8\uc2b5\ub2c8\ub2e4.", columnName) : "\ub370\uc774\ud130 \uae38\uc774\uac00 \ucd5c\ub300 \ud5c8\uc6a9 \uae38\uc774\ub97c \ucd08\uacfc\ud588\uc2b5\ub2c8\ub2e4.";
            errorCode = ErrorCode.DATA_TRUNCATION_ERROR;
        }
        this.logJumpableStackTrace((Throwable) ex);
        ApiResponse response = ApiResponse.error(errorCode, (String) message);
        response.setErrorId(errorId);
        return ResponseEntity.status((HttpStatusCode) HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String location = stackTrace.length > 0 ? stackTrace[0].getClassName() + "." + stackTrace[0].getMethodName() + "(" + stackTrace[0].getFileName() + ":" + stackTrace[0].getLineNumber() + ")" : "unknown";
        String message = String.format("\ub110 \ucc38\uc870 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4 (\uc704\uce58: %s)", location);
        return this.buildErrorResponse(ex, ErrorCode.NULL_POINT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(value = {IOException.class})
    protected ResponseEntity<?> handleIOException(IOException ex) {
        return this.buildErrorResponse(ex, ErrorCode.IO_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "\uc785\ucd9c\ub825 \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4: " + ex.getMessage());
    }

    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String message = String.format("\uc9c0\uc6d0\ud558\uc9c0 \uc54a\ub294 \ubbf8\ub514\uc5b4 \ud0c0\uc785\uc785\ub2c8\ub2e4: %s. \uc9c0\uc6d0 \ud0c0\uc785: %s", ex.getContentType(), ex.getSupportedMediaTypes());
        return this.buildErrorResponse((Exception) ex, ErrorCode.HTTP_MEDIA_TYPE_NOT_SUPPORTED_ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }

    @ExceptionHandler(value = {ImageFileIsTooBigException.class})
    public ResponseEntity<?> handleImageFileIsTooBigException(ImageFileIsTooBigException ex) {
        return this.buildErrorResponse(ex, ErrorCode.IMAGE_FILE_TOO_BIG_ERROR, HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage());
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    protected ResponseEntity<?> noSuchElementException(NoSuchElementException ex) {
        return this.buildErrorResponse(ex, ErrorCode.NO_SUCH_ELEMENT, HttpStatus.NOT_FOUND, "\uc694\uccad\ud55c \ub370\uc774\ud130\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: " + ex.getMessage());
    }

    @ExceptionHandler(value = {CEH_ParametersAreNotExist.class})
    protected ResponseEntity<?> parametersAreNotExists(CEH_ParametersAreNotExist ex) {
        return this.buildErrorResponse(ex, ErrorCode.PARAMETERS_ARE_NOT_EXIST, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleCustomException(RuntimeException ex) {
        return buildErrorResponse(ex, ex.getErrorCode(),
                HttpStatus.valueOf(ex.getErrorCode().getStatus()), ex.getMessage());
    }

    @ExceptionHandler(value = {JsonParseException.class, JsonProcessingException.class})
    public ResponseEntity<?> handleJsonProcessingException(Exception ex) {
        return this.buildErrorResponse(ex, ErrorCode.API_JSON_PARSING_ERROR, HttpStatus.BAD_REQUEST, "JSON \ub370\uc774\ud130 \ud30c\uc2f1 \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4. \uc62c\ubc14\ub978 \ud615\uc2dd\uc778\uc9c0 \ud655\uc778\ud558\uc138\uc694.");
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<?> handleAllExceptions(Exception ex) {
        return this.buildErrorResponse(ex, ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "\uc11c\ubc84 \ub0b4\ubd80 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4. \uad00\ub9ac\uc790\uc5d0\uac8c \ubb38\uc758\ud558\uc138\uc694.");
    }

    private boolean isDevelopmentEnvironment() {
        String activeProfile = System.getProperty("spring.profiles.active", "");
        return activeProfile.equals("local") || activeProfile.equals("dev") || activeProfile.equals("test");
    }
}

