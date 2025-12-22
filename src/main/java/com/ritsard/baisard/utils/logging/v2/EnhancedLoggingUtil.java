/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  jakarta.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.ritsard.baisard.utils.logging.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class EnhancedLoggingUtil {
    private static final Logger log = LoggerFactory.getLogger(EnhancedLoggingUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    private static final String APP_PACKAGE = "com.lodong";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String logException(Throwable ex, String errorType) {
        String errorId = EnhancedLoggingUtil.generateErrorId();
        MDC.put((String)"errorId", (String)errorId);
        try {
            log.error("\u26a0\ufe0f [{}] \uc5d0\ub7ec \ubc1c\uc0dd [ID:{}]: {}", new Object[]{errorType, errorId, ex.getMessage()});
            EnhancedLoggingUtil.logRequestInfo();
            EnhancedLoggingUtil.logJumpableStackTrace(ex);
            Throwable rootCause = EnhancedLoggingUtil.getRootCause(ex);
            if (rootCause != ex) {
                log.error("\ud83d\udca5 \uadfc\ubcf8 \uc6d0\uc778: {} - {}", (Object)rootCause.getClass().getSimpleName(), (Object)rootCause.getMessage());
                EnhancedLoggingUtil.logJumpableStackTrace(rootCause);
            }
            String string = errorId;
            return string;
        }
        finally {
            MDC.remove((String)"errorId");
        }
    }

    public static void logRequestObject(Object requestObject) {
        if (requestObject == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(requestObject);
            log.error("\ud83d\udce6 \uc694\uccad \uac1d\uccb4 \uad6c\uc870:\n{}", (Object)json);
        }
        catch (Exception e) {
            log.error("\uc694\uccad \uac1d\uccb4 \uc9c1\ub82c\ud654 \uc2e4\ud328: {}", (Object)e.getMessage());
        }
    }

    public static void logJumpableStackTrace(Throwable ex) {
        if (ex == null || ex.getStackTrace() == null) {
            return;
        }
        String controllerInfo = null;
        String serviceInfo = null;
        String repositoryInfo = null;
        StringBuilder importantFrames = new StringBuilder();
        importantFrames.append("\n\ud83d\udd0d \uc911\uc694 \uc2a4\ud0dd \ud2b8\ub808\uc774\uc2a4:");
        boolean foundImportant = false;
        for (StackTraceElement element : ex.getStackTrace()) {
            String className = element.getClassName();
            if (!className.startsWith(APP_PACKAGE)) continue;
            String frame = String.format("\n    %s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
            importantFrames.append(frame);
            foundImportant = true;
            if (className.contains("Controller") && controllerInfo == null) {
                controllerInfo = frame;
                continue;
            }
            if (className.contains("Service") && serviceInfo == null) {
                serviceInfo = frame;
                continue;
            }
            if (!className.contains("Repository") || repositoryInfo != null) continue;
            repositoryInfo = frame;
        }
        StringBuilder summary = new StringBuilder("\n\ud83d\udccd \ud575\uc2ec \uc704\uce58 \uc815\ubcf4:");
        boolean hasSummary = false;
        if (controllerInfo != null) {
            summary.append("\n    \ud83c\udfae Controller: ").append(controllerInfo);
            hasSummary = true;
        }
        if (serviceInfo != null) {
            summary.append("\n    \ud83d\udd27 Service: ").append(serviceInfo);
            hasSummary = true;
        }
        if (repositoryInfo != null) {
            summary.append("\n    \ud83d\udcbe Repository: ").append(repositoryInfo);
            hasSummary = true;
        }
        if (hasSummary) {
            log.error(summary.toString());
        }
        if (foundImportant) {
            log.error(importantFrames.toString());
        } else {
            log.error("\n\ud83d\udd0d \uc2a4\ud0dd \ud2b8\ub808\uc774\uc2a4 (\ucc98\uc74c 3\uac1c):");
            int count = 0;
            for (StackTraceElement element : ex.getStackTrace()) {
                if (count++ >= 3) break;
                log.error("    {}.{}({}:{})", new Object[]{element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()});
            }
        }
    }

    private static void logRequestInfo() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                log.error("\ud83c\udf10 \uc694\uccad \uc815\ubcf4: {} {}", (Object)request.getMethod(), (Object)request.getRequestURI());
                HashMap params = new HashMap();
                request.getParameterMap().forEach((key, values) -> {
                    String value = String.join((CharSequence)", ", values);
                    if (EnhancedLoggingUtil.isSensitiveParam(key)) {
                        params.put(key, "********");
                    } else {
                        params.put(key, value);
                    }
                });
                if (!params.isEmpty()) {
                    log.error("\ud83d\udcdd \uc694\uccad \ud30c\ub77c\ubbf8\ud130: {}", params);
                }
                HashMap headers = new HashMap();
                Collections.list(request.getHeaderNames()).forEach(name -> {
                    if (!EnhancedLoggingUtil.isSensitiveHeader(name)) {
                        headers.put(name, request.getHeader(name));
                    }
                });
                if (!headers.isEmpty()) {
                    log.error("\ud83d\udd16 \uc8fc\uc694 \ud5e4\ub354: {}", headers);
                }
            }
        }
        catch (Exception e) {
            log.debug("\uc694\uccad \uc815\ubcf4 \ub85c\uae45 \uc2e4\ud328: {}", (Object)e.getMessage());
        }
    }

    public static Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        int maxDepth = 10;
        for (int depth = 0; cause.getCause() != null && cause != cause.getCause() && depth < maxDepth; cause = cause.getCause(), ++depth) {
        }
        return cause;
    }

    protected static String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static boolean isSensitiveParam(String paramName) {
        if (paramName == null) {
            return false;
        }
        String lowerName = paramName.toLowerCase();
        return lowerName.contains("password") || lowerName.contains("token") || lowerName.contains("key") || lowerName.contains("secret") || lowerName.contains("credential") || lowerName.contains("auth");
    }

    private static boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") || lowerName.contains("cookie") || lowerName.contains("token") || lowerName.contains("secret") || lowerName.contains("key");
    }
}

