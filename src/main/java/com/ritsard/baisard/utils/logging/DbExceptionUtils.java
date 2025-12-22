/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  jakarta.servlet.http.HttpServletRequest
 *  org.hibernate.exception.DataException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.orm.jpa.JpaSystemException
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.ritsard.baisard.utils.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritsard.baisard.utils.enums.ErrorCode;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 데이터베이스 오류 처리 전용 유틸리티 클래스
 * GlobalExceptionHandler에서 사용하기 위한 헬퍼 메서드 제공
 */
public class DbExceptionUtils {
    private static final Logger log = LoggerFactory.getLogger(DbExceptionUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JPA 시스템 예외 상세 정보 추출 및 로깅
     */
    public static Map<String, Object> processJpaException(JpaSystemException ex, String errorId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 근본 원인 파악
        Throwable rootCause = getRootCause(ex);
        String errorMessage = rootCause.getMessage();

        // 2. 요청 정보 수집 시도 (여러 방법으로)
        Map<String, Object> requestInfo = collectRequestInfo();

        // 3. 요청 파라미터와 바디 정보 수집 시도
        Map<String, Object> requestDetails = collectRequestDetails();

        // 4. SQL 상태와 오류 코드 추출 (가능한 경우)
        Map<String, Object> sqlInfo = extractSqlInfo(rootCause);

        // 5. 애플리케이션 컨텍스트 정보 추출
        Map<String, Object> appContext = extractAppContext(ex);

        // 6. 오류 유형별 메시지 커스터마이징
        String customMessage = "데이터베이스 작업 중 오류가 발생했습니다";
        ErrorCode errorCode = ErrorCode.DATABASE_ERROR; // 기본 데이터베이스 오류 코드

        if (errorMessage.contains("Data truncated for column")) {
            // 데이터 길이 초과 오류 처리
            String columnName = extractColumnName(errorMessage);
            if (columnName != null) {
                customMessage = String.format("컬럼 '%s'에 너무 긴 데이터가 입력되었습니다. 데이터 길이를 확인하세요.", columnName);

                // 컬럼 값 추정 시도 (요청 파라미터에서)
                Object columnValue = findColumnValueInRequest(columnName, requestDetails);
                if (columnValue != null) {
                    customMessage += String.format(" 시도한 값: '%s'", columnValue);
                }
            } else {
                customMessage = "일부 데이터가 너무 길어 잘렸습니다. 입력 데이터의 길이를 확인하세요.";
            }
            log.error("[데이터 길이 초과] 오류 ID: {}, 컬럼: {}, 메시지: {}",
                    errorId, columnName, errorMessage);
            errorCode = ErrorCode.DATA_TRUNCATION_ERROR;
        } else if (errorMessage.toLowerCase().contains("foreign key")) {
            customMessage = "참조 무결성 제약조건 위반. 참조하는 데이터가 존재하는지 확인하세요.";
            errorCode = ErrorCode.FOREIGN_KEY_VIOLATION_ERROR;
        } else {
            log.error("[데이터베이스 오류] 오류 ID: {}, 메시지: {}", errorId, errorMessage);
        }

        // 7. 상세 로깅 - 요청 정보
        if (!requestInfo.isEmpty()) {
            log.error("요청 정보: {} {}", requestInfo.get("method"), requestInfo.get("uri"));

            if (!requestDetails.isEmpty()) {
                try {
                    log.error("요청 세부 정보: {}", objectMapper.writeValueAsString(requestDetails));
                } catch (Exception e) {
                    log.error("요청 세부 정보: {}", requestDetails);
                }
            }
        } else {
            log.error("요청 정보: 사용할 수 없음 (요청 컨텍스트 종료됨)");
        }

        // 8. 상세 로깅 - SQL 정보
        if (!sqlInfo.isEmpty()) {
            log.error("SQL 정보: {}", sqlInfo);
        }

        // 9. 상세 로깅 - 중요 스택 트레이스 (애플리케이션 코드)
        String stackTrace = formatImportantStackTrace(ex);
        log.error("중요 스택 트레이스: \n{}", stackTrace);

        // 10. 상세 로깅 - 근본 원인이 다른 경우
        if (rootCause != ex) {
            String rootCauseStack = formatImportantStackTrace(rootCause);
            log.error("근본 원인 스택 트레이스: \n{}", rootCauseStack);
        }

        // 11. 결과 반환
        result.put("customMessage", customMessage);
        result.put("errorCode", errorCode);
        result.put("rootCause", rootCause.getClass().getName());
        result.put("requestInfo", requestInfo);
        result.put("sqlInfo", sqlInfo);
        result.put("appContext", appContext);

        return result;
    }

    /**
     * 예외의 근본 원인 찾기
     */
    public static Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        int depth = 0;
        int maxDepth = 10; // 최대 깊이 제한 (무한 루프 방지)

        while (cause.getCause() != null && cause != cause.getCause() && depth < maxDepth) {
            cause = cause.getCause();
            depth++;
        }

        return cause;
    }

    /**
     * SQL 오류 메시지에서 컬럼명 추출
     */
    public static String extractColumnName(String message) {
        if (message == null) return null;

        // "Data truncated for column 'category' at row 1" 형식 처리
        if (message.contains("Data truncated for column")) {
            int startIndex = message.indexOf("'") + 1;
            int endIndex = message.indexOf("'", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    /**
     * 요청 정보 수집 (여러 방법으로 시도)
     */
    public static Map<String, Object> collectRequestInfo() {
        Map<String, Object> info = new HashMap<>();

        // 1. RequestContextHolder 통해 시도
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                info.put("method", request.getMethod());
                info.put("uri", request.getRequestURI());
                info.put("remoteAddr", request.getRemoteAddr());
                info.put("userAgent", request.getHeader("User-Agent"));

                // 요청 ID가 있으면 추가
                String requestId = (String) request.getAttribute("requestId");
                if (requestId != null) {
                    info.put("requestId", requestId);
                }

                return info;
            }
        } catch (Exception e) {
            log.debug("RequestContextHolder로 요청 정보 획득 실패: {}", e.getMessage());
        }

        // 2. MDC에서 정보 획득 시도
        try {
            String method = MDC.get("method");
            String uri = MDC.get("uri");

            if (method != null) info.put("method", method);
            if (uri != null) info.put("uri", uri);

            String remoteAddr = MDC.get("remoteAddr");
            String userAgent = MDC.get("userAgent");

            if (remoteAddr != null) info.put("remoteAddr", remoteAddr);
            if (userAgent != null) info.put("userAgent", userAgent);
        } catch (Exception e) {
            log.debug("MDC에서 요청 정보 획득 실패: {}", e.getMessage());
        }

        return info;
    }

    /**
     * 요청 파라미터와 바디 수집 시도
     */
    private static Map<String, Object> collectRequestDetails() {
        Map<String, Object> details = new HashMap<>();

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                // 요청 파라미터 수집
                Map<String, String[]> paramMap = request.getParameterMap();
                if (paramMap != null && !paramMap.isEmpty()) {
                    Map<String, Object> params = new HashMap<>();
                    for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                        String key = entry.getKey();
                        String[] values = entry.getValue();

                        // 민감 정보 필터링
                        if (isSensitiveParam(key)) {
                            params.put(key, "******");
                        } else {
                            params.put(key, values.length == 1 ? values[0] : Arrays.asList(values));
                        }
                    }
                    details.put("parameters", params);
                }

                // 요청 속성 수집 (제한적)
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("contentType", request.getContentType());
                attributes.put("characterEncoding", request.getCharacterEncoding());
                attributes.put("contentLength", request.getContentLengthLong());
                details.put("attributes", attributes);
            }
        } catch (Exception e) {
            log.debug("요청 상세 정보 수집 실패: {}", e.getMessage());
        }

        return details;
    }

    /**
     * SQL 관련 정보 추출
     */
    private static Map<String, Object> extractSqlInfo(Throwable cause) {
        Map<String, Object> sqlInfo = new HashMap<>();

        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            sqlInfo.put("sqlState", sqlEx.getSQLState());
            sqlInfo.put("errorCode", sqlEx.getErrorCode());
            sqlInfo.put("message", sqlEx.getMessage());
        } else if (cause instanceof DataException) {
            DataException dataEx = (DataException) cause;
            sqlInfo.put("sqlState", dataEx.getSQLState());
            sqlInfo.put("errorCode", dataEx.getErrorCode());
            sqlInfo.put("message", dataEx.getMessage());
        }

        return sqlInfo;
    }

    /**
     * 애플리케이션 컨텍스트 정보 추출
     */
    private static Map<String, Object> extractAppContext(Exception ex) {
        Map<String, Object> context = new HashMap<>();

        // 중요 스택 트레이스에서 정보 추출
        StackTraceElement[] stack = ex.getStackTrace();

        // 애플리케이션 관련 프레임 찾기
        for (StackTraceElement frame : stack) {
            String className = frame.getClassName();

            if (className.startsWith("com.lodong")) {
                // 중요 컴포넌트 식별
                if (className.contains("Controller")) {
                    context.put("controller", className);
                    context.put("controllerMethod", frame.getMethodName());
                    context.put("controllerLine", frame.getLineNumber());
                } else if (className.contains("Service")) {
                    context.put("service", className);
                    context.put("serviceMethod", frame.getMethodName());
                    context.put("serviceLine", frame.getLineNumber());
                } else if (className.contains("Repository")) {
                    context.put("repository", className);
                    context.put("repositoryMethod", frame.getMethodName());
                    context.put("repositoryLine", frame.getLineNumber());
                }
            }
        }

        return context;
    }

    /**
     * 중요 스택 트레이스 포맷팅
     */
    public static String formatImportantStackTrace(Throwable ex) {
        return Arrays.stream(ex.getStackTrace())
                .filter(frame -> frame.getClassName().startsWith("com.lodong"))
                .limit(10)
                .map(frame -> String.format("    %s.%s(%s:%d)",
                        frame.getClassName(),
                        frame.getMethodName(),
                        frame.getFileName(),
                        frame.getLineNumber()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 요청에서 컬럼 값 찾기 시도
     */
    private static Object findColumnValueInRequest(String columnName, Map<String, Object> requestDetails) {
        if (requestDetails.isEmpty() || columnName == null) {
            return null;
        }

        try {
            // 파라미터에서 검색
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) requestDetails.getOrDefault("parameters", Collections.emptyMap());

            // 1. 정확히 같은 이름으로 검색
            if (params.containsKey(columnName)) {
                return params.get(columnName);
            }

            // 2. 대소문자 무시하고 검색
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(columnName)) {
                    return entry.getValue();
                }
            }

            // 3. 비슷한 이름 (camelCase, snake_case 등) 검색
            String normalized = columnName.toLowerCase().replace("_", "");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey().toLowerCase().replace("_", "");
                if (key.equals(normalized)) {
                    return entry.getValue();
                }
            }
        } catch (Exception e) {
            log.debug("컬럼 값 검색 실패: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 민감한 파라미터인지 확인
     */
    private static boolean isSensitiveParam(String paramName) {
        if (paramName == null) return false;

        String lowerName = paramName.toLowerCase();
        return lowerName.contains("password") ||
                lowerName.contains("token") ||
                lowerName.contains("key") ||
                lowerName.contains("secret") ||
                lowerName.contains("credential") ||
                lowerName.contains("auth");
    }
}