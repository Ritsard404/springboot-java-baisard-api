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
    private static final String APP_PACKAGE = "com.ritsard";
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
        MDC.put("errorId", errorId);

        // Default custom message and error code
        String customMessage = "An SQL error occurred while processing the database operation.";
        ErrorCode errorCode = ErrorCode.SQL_ERROR;

        int sqlErrorCode = ex.getErrorCode();
        String sqlState = ex.getSQLState();

        log.error("\n==================== SQL ERROR DETAILS ====================\n" +
                        "📝 Error ID: {}\n" +
                        "📍 Request Info:\n   - URL: {} {}\n   - Client IP: {}\n" +
                        "🔴 SQL Error:\n   - Error Code: {}\n   - SQL State: {}\n   - Message: {}",
                errorId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(),
                sqlErrorCode, sqlState, ex.getMessage());

        // Customize messages based on SQL error codes
        if (sqlErrorCode == 1406) {
            customMessage = "The value for the column is too large. Please check the input data.";
            errorCode = ErrorCode.DATA_TRUNCATION_ERROR;
        } else if (sqlErrorCode == 1062) {
            customMessage = "Duplicate data exists. Please check the value being inserted.";
            errorCode = ErrorCode.UNIQUE_CONSTRAINT_ERROR;
        } else if (sqlErrorCode == 1452) {
            customMessage = "Foreign key constraint violation. The referenced data does not exist.";
            errorCode = ErrorCode.FOREIGN_KEY_VIOLATION_ERROR;
        }

        this.logJumpableStackTrace(ex);

        ApiResponse response = ApiResponse.error(errorCode, customMessage);
        response.setErrorId(errorId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.UNAUTHORIZED_ERROR, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorId = this.generateErrorId();
        MDC.put("errorId", errorId);

        log.error("⚠️ [Invalid Argument Error] [ID:{}]: {}", errorId, ex.getMessage());
        this.logJumpableStackTrace(ex);

        ApiResponse response = ApiResponse.error(ErrorCode.BAD_REQUEST_ERROR, ex.getMessage());
        response.setErrorId(errorId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<?> buildErrorResponse(Exception ex, ErrorCode errorCode, HttpStatus status, String customMessage) {
        String errorId = this.generateErrorId();
        MDC.put("errorId", errorId);

        try {
            HttpServletRequest request = null;
            try {
                ServletWebRequest attrs = (ServletWebRequest) RequestContextHolder.getRequestAttributes();
                if (attrs != null) request = attrs.getRequest();
            } catch (Exception e) {
                log.debug("Error retrieving request info: {}", e.getMessage());
            }

            if (request != null) {
                Map<String, String[]> params;
                StringBuilder logMessage = new StringBuilder("\n==================== ERROR DETAILS ====================\n");
                logMessage.append(String.format("📝 Error ID: %s\n", errorId));
                logMessage.append("📍 Request Info:\n");
                logMessage.append(String.format("   - URL: %s %s\n", request.getMethod(), request.getRequestURI()));

                if (request.getQueryString() != null) {
                    logMessage.append(String.format("   - Query String: %s\n", request.getQueryString()));
                }

                if (!(params = request.getParameterMap()).isEmpty()) {
                    logMessage.append("   - Parameters:\n");
                    params.forEach((key, values) -> {
                        String value = String.join(", ", values);
                        if (!this.isSensitiveParam(key)) {
                            logMessage.append(String.format("      * %s: %s\n", key, value));
                        } else {
                            logMessage.append(String.format("      * %s: [MASKED]\n", key));
                        }
                    });
                }

                if (request.getContentType() != null) {
                    logMessage.append(String.format("   - Content-Type: %s\n", request.getContentType()));
                }

                logMessage.append(String.format("   - Client IP: %s\n", request.getRemoteAddr()));
                logMessage.append(String.format("   - User-Agent: %s\n", request.getHeader("User-Agent")));
                logMessage.append("🔴 Exception:\n");
                logMessage.append(String.format("   - Type: %s\n", ex.getClass().getSimpleName()));
                logMessage.append(String.format("   - Message: %s\n", customMessage));
                if (ex.getMessage() != null && !ex.getMessage().equals(customMessage)) {
                    logMessage.append(String.format("   - Original Message: %s\n", ex.getMessage()));
                }
                log.error(logMessage.toString());
            } else {
                log.error("⚠️ [{}] occurred [ID:{}]: {}", ex.getClass().getSimpleName(), errorId, customMessage);
            }

            this.logJumpableStackTrace(ex);

            Throwable rootCause = this.getRootCause(ex);
            if (rootCause != ex) {
                log.error("💥 Root Cause: {} - {}", rootCause.getClass().getSimpleName(), rootCause.getMessage());
                if (!rootCause.getClass().equals(ex.getClass())) {
                    this.logJumpableStackTrace(rootCause);
                }
            }

            ApiResponse response = ApiResponse.error(errorCode, customMessage);
            response.setErrorId(errorId);
            return ResponseEntity.status(status).body(response);
        } finally {
            MDC.remove("errorId");
        }
    }

    private void logJumpableStackTrace(Throwable ex) {
        if (ex == null || ex.getStackTrace() == null) return;

        ArrayList<String> controllerFrames = new ArrayList<>();
        ArrayList<String> serviceFrames = new ArrayList<>();
        ArrayList<String> repositoryFrames = new ArrayList<>();
        ArrayList<String> otherAppFrames = new ArrayList<>();

        for (StackTraceElement element : ex.getStackTrace()) {
            String className = element.getClassName();
            if (!className.startsWith(APP_PACKAGE)) continue;

            String frame = String.format("%s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());

            if (className.contains("Controller")) controllerFrames.add(frame);
            else if (className.contains("Service")) serviceFrames.add(frame);
            else if (className.contains("Repository")) repositoryFrames.add(frame);
            else otherAppFrames.add(frame);
        }

        StringBuilder stackLog = new StringBuilder("\n📌 Stack Trace Analysis:");
        if (!controllerFrames.isEmpty()) {
            stackLog.append("\n🎮 Controller:");
            controllerFrames.forEach(frame -> stackLog.append("\n    ").append(frame));
        }
        if (!serviceFrames.isEmpty()) {
            stackLog.append("\n🔧 Service:");
            serviceFrames.forEach(frame -> stackLog.append("\n    ").append(frame));
        }
        if (!repositoryFrames.isEmpty()) {
            stackLog.append("\n💾 Repository:");
            repositoryFrames.forEach(frame -> stackLog.append("\n    ").append(frame));
        }
        if (!otherAppFrames.isEmpty()) {
            stackLog.append("\n📦 Other Application Frames:");
            otherAppFrames.stream().limit(5).forEach(frame -> stackLog.append("\n    ").append(frame));
        }

        if (controllerFrames.isEmpty() && serviceFrames.isEmpty() && repositoryFrames.isEmpty() && otherAppFrames.isEmpty()) {
            stackLog.append("\n⚠️ No application-specific frames found. First 3 stack trace elements:");
            for (int i = 0; i < Math.min(3, ex.getStackTrace().length); i++) {
                StackTraceElement element = ex.getStackTrace()[i];
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
                log.error("🌐 Request Info: {} {}", request.getMethod(), request.getRequestURI());
                if (request.getQueryString() != null) {
                    log.error("🔍 Query String: {}", request.getQueryString());
                }
                HashMap<String, String> params = new HashMap<>();
                request.getParameterMap().forEach((key, values) -> {
                    String value = String.join(", ", values);
                    if (this.isSensitiveParam(key)) {
                        params.put(key, "********");
                    } else {
                        params.put(key, value);
                    }
                });
                if (!params.isEmpty()) {
                    log.error("📝 Request Parameters: {}", params);
                }
            }
        } catch (Exception e) {
            log.debug("Failed to retrieve request info: {}", e.getMessage());
        }
    }

    private void logRequestObject(Object requestObject) {
        if (requestObject == null) return;
        try {
            String json = objectMapper.writeValueAsString(requestObject);
            log.error("📦 Request Object Details:\n{}", json);
        } catch (Exception e) {
            log.error("Failed to serialize request object: {}", e.getMessage());
        }
    }

    private boolean isSensitiveParam(String paramName) {
        if (paramName == null) return false;
        String lowerName = paramName.toLowerCase();
        return lowerName.contains("password") || lowerName.contains("token") || lowerName.contains("key")
                || lowerName.contains("secret") || lowerName.contains("credential") || lowerName.contains("auth");
    }

    private Map<String, Object> collectErrorDetails(Exception ex, String errorType) {
        HashMap<String, Object> details = new HashMap<>();
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
            HttpServletRequest request = ((ServletWebRequest) RequestContextHolder.getRequestAttributes()).getRequest();
            details.put("requestUri", request.getRequestURI());
            details.put("requestMethod", request.getMethod());
            details.put("queryString", request.getQueryString());
            details.put("remoteAddr", request.getRemoteAddr());
            details.put("userAgent", request.getHeader("User-Agent"));

            Object userId = request.getAttribute("userId");
            if (userId != null) {
                MDC.put("userId", userId.toString());
                details.put("userId", userId);
            }

            String requestId = (String) request.getAttribute("requestId");
            if (requestId != null && !requestId.isEmpty()) {
                MDC.put("requestId", requestId);
                details.put("requestId", requestId);
            }
        } catch (Exception e) {
            details.put("requestUri", "Request info unavailable");
            details.put("requestMethod", "UNKNOWN");
        }

        return details;
    }

    private StackTraceElement findRelevantStackFrame(StackTraceElement[] stackTrace) {
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith(APP_PACKAGE)) return element;
        }
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains("Controller") || element.getClassName().contains("Service"))
                return element;
        }
        return stackTrace[0];
    }

    private String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @ExceptionHandler(value = {ProcessorException.class})
    public ResponseEntity<?> handleProcessorException(ProcessorException ex, WebRequest request) {
        String message = String.format("%s (Processor: %s)", ex.getMessage(), ex.getProcessorName());
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
        String message = String.format("%s (Retry after %d seconds)", ex.getMessage(), ex.getRetryAfterSeconds());
        ResponseEntity<?> response = this.buildErrorResponse(ex, ErrorCode.RATE_LIMIT_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS, message);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(response.getBody());
    }

    @ExceptionHandler(value = {TokenLimitExceededException.class})
    public ResponseEntity<?> handleTokenLimitExceededException(TokenLimitExceededException ex, WebRequest request) {
        String message = String.format("%s (Current: %d, Max: %d)", ex.getMessage(), ex.getCurrentTokenCount(), ex.getMaxTokens());
        return this.buildErrorResponse(ex, ErrorCode.TOKEN_LIMIT_EXCEEDED, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {ContentFilteringException.class})
    public ResponseEntity<?> handleContentFilteringException(ContentFilteringException ex, WebRequest request) {
        String message = String.format("%s (Filtered Category: %s)", ex.getMessage(), ex.getFilteredCategory());
        return this.buildErrorResponse(ex, ErrorCode.CONTENT_FILTERED, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(value = {TimeoutException.class, SocketTimeoutException.class})
    public ResponseEntity<?> handleTimeoutException(Exception ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.API_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT, "Request timed out.");
    }

    @ExceptionHandler(value = {ServerErrorException.class})
    public ResponseEntity<?> handleServerErrorException(ServerErrorException ex, WebRequest request) {
        ErrorCode errorCode = ex.getStatusCode() == 502 ? ErrorCode.BAD_GATEWAY :
                ex.getStatusCode() == 503 ? ErrorCode.SERVICE_UNAVAILABLE :
                        ErrorCode.API_SERVER_ERROR;
        return this.buildErrorResponse(ex, errorCode, HttpStatus.valueOf((int) ex.getStatusCode()), ex.getMessage());
    }

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<?> handleRequestException(RequestException ex, WebRequest request) {
        ErrorCode errorCode = ex.isUnauthorized() ? ErrorCode.API_UNAUTHORIZED :
                ex.isForbidden() ? ErrorCode.API_FORBIDDEN :
                        ex.isNotFound() ? ErrorCode.RESOURCE_NOT_FOUND :
                                ex.isRateLimited() ? ErrorCode.RATE_LIMIT_EXCEEDED :
                                        ErrorCode.API_BAD_REQUEST;
        return this.buildErrorResponse(ex, errorCode, HttpStatus.valueOf((int) ex.getStatusCode()), ex.getMessage());
    }

    @ExceptionHandler(value = {NetworkException.class})
    public ResponseEntity<?> handleNetworkException(NetworkException ex, WebRequest request) {
        return this.buildErrorResponse(ex, ErrorCode.NETWORK_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
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
        MDC.put("errorId", errorId);

        // Default message and error code
        Object message = "The item you are trying to use already exists.";
        ErrorCode errorCode = ErrorCode.DUPLICATE_ERROR;

        // Get the root cause message
        Throwable rootCause = this.getRootCause(ex);
        String errorMessage = rootCause.getMessage();

        // Log the error with ID and message
        log.error("\ud83d\udcdb [Data Integrity Violation] Error ID: {}, Message: {}", errorId, errorMessage);

        // Log request info if available
        Map<String, Object> requestInfo = DbExceptionUtils.collectRequestInfo();
        if (!requestInfo.isEmpty()) {
            log.error("\ud83c\udf10 Request Info: {} {}", requestInfo.get("method"), requestInfo.get("uri"));
        }

        // Customize message based on specific constraint or error
        if (errorMessage.contains("uk_base_member_phone_is_deleted")) {
            message = "The phone number you entered is already registered.";
        } else if (errorMessage.contains("uk_base_member_email_is_deleted")) {
            message = "The email address you entered is already registered.";
        } else if (errorMessage.contains("uk_")) {
            String constraintName = errorMessage.contains("uk_") ? errorMessage.substring(errorMessage.indexOf("uk_")) : errorMessage;
            message = "The item you entered violates a unique constraint: " + constraintName;
            errorCode = ErrorCode.UNIQUE_CONSTRAINT_ERROR;
        } else if (errorMessage.toLowerCase().contains("foreign key")) {
            message = "The item cannot be processed because it is referenced by another record.";
            errorCode = ErrorCode.FOREIGN_KEY_VIOLATION_ERROR;
        } else if (errorMessage.contains("Data truncated")) {
            String columnName = DbExceptionUtils.extractColumnName(errorMessage);
            message = columnName != null
                    ? String.format("The column '%s' received a value that was too long.", columnName)
                    : "A value was too large to fit in the database column.";
            errorCode = ErrorCode.DATA_TRUNCATION_ERROR;
        }

        // Log full stack trace
        this.logJumpableStackTrace(ex);

        // Build API response with error ID
        ApiResponse response = ApiResponse.error(errorCode, (String) message);
        response.setErrorId(errorId);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }


    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String location = stackTrace.length > 0
                ? stackTrace[0].getClassName() + "." + stackTrace[0].getMethodName() +
                "(" + stackTrace[0].getFileName() + ":" + stackTrace[0].getLineNumber() + ")"
                : "unknown";
        String message = String.format("A null pointer exception occurred (Location: %s)", location);
        return this.buildErrorResponse(ex, ErrorCode.NULL_POINT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(value = {IOException.class})
    protected ResponseEntity<?> handleIOException(IOException ex) {
        return this.buildErrorResponse(ex, ErrorCode.IO_ERROR, HttpStatus.INTERNAL_SERVER_ERROR,
                "An I/O error occurred during processing: " + ex.getMessage());
    }

    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String message = String.format("Unsupported media type: %s. Supported types: %s",
                ex.getContentType(), ex.getSupportedMediaTypes());
        return this.buildErrorResponse(ex, ErrorCode.HTTP_MEDIA_TYPE_NOT_SUPPORTED_ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }

    @ExceptionHandler(value = {ImageFileIsTooBigException.class})
    public ResponseEntity<?> handleImageFileIsTooBigException(ImageFileIsTooBigException ex) {
        return this.buildErrorResponse(ex, ErrorCode.IMAGE_FILE_TOO_BIG_ERROR, HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage());
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    protected ResponseEntity<?> noSuchElementException(NoSuchElementException ex) {
        return this.buildErrorResponse(ex, ErrorCode.NO_SUCH_ELEMENT, HttpStatus.NOT_FOUND,
                "The requested element could not be found: " + ex.getMessage());
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
        return this.buildErrorResponse(ex, ErrorCode.API_JSON_PARSING_ERROR, HttpStatus.BAD_REQUEST,
                "A JSON parsing error occurred. Please verify that the input is correctly formatted.");
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<?> handleAllExceptions(Exception ex) {
        return this.buildErrorResponse(ex, ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred. Please contact the administrator.");
    }

    @ExceptionHandler(value = {org.springframework.security.authorization.AuthorizationDeniedException.class})
    public ResponseEntity<?> handleAuthorizationDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException ex,
            HttpServletRequest request) {

        String errorId = this.generateErrorId();
        MDC.put("errorId", errorId);

        log.warn("\n==================== AUTHORIZATION DENIED ====================\n" +
                        "🆔 Error ID: {}\n" +
                        "📍 Request Info:\n   - URL: {} {}\n   - Client IP: {}\n" +
                        "🔴 Message: {}",
                errorId, request.getMethod(), request.getRequestURI(),
                request.getRemoteAddr(), ex.getMessage());

        ApiResponse response = ApiResponse.error(
                ErrorCode.FORBIDDEN_ERROR,
                "You do not have permission to access this resource"
        );
        response.setErrorId(errorId);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String errorId = this.generateErrorId();
        MDC.put("errorId", errorId);

        log.warn("\n==================== ACCESS DENIED ====================\n" +
                        "🆔 Error ID: {}\n" +
                        "📍 Request Info:\n   - URL: {} {}\n   - Client IP: {}\n" +
                        "🔴 Message: {}",
                errorId, request.getMethod(), request.getRequestURI(),
                request.getRemoteAddr(), ex.getMessage());

        ApiResponse response = ApiResponse.error(
                ErrorCode.FORBIDDEN_ERROR,
                "You do not have permission to access this resource"
        );
        response.setErrorId(errorId);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    private boolean isDevelopmentEnvironment() {
        String activeProfile = System.getProperty("spring.profiles.active", "");
        return activeProfile.equals("local") || activeProfile.equals("dev") || activeProfile.equals("test");
    }

}

