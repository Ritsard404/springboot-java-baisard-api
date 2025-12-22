/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 */
package com.ritsard.baisard.utils.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class LoggingUtils {
    private static final Logger log = LoggerFactory.getLogger(LoggingUtils.class);
    private static final String APP_PACKAGE = "com.ritsard";

    public static void setupRequestContext(HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put((String)"requestId", (String)requestId);
        MDC.put((String)"uri", (String)request.getRequestURI());
        MDC.put((String)"method", (String)request.getMethod());
        MDC.put((String)"remoteAddr", (String)request.getRemoteAddr());
        MDC.put((String)"userAgent", (String)request.getHeader("User-Agent"));
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            MDC.put((String)"userId", (String)userId.toString());
        }
    }

    public static <T> T measureExecution(String operationName, Supplier<T> operation) {
        long startTime = System.currentTimeMillis();
        try {
            T result = operation.get();
            long duration = System.currentTimeMillis() - startTime;
            MDC.put((String)"executionTime", (String)String.valueOf(duration));
            if (duration > 1000L) {
                log.warn("\uc131\ub2a5 \uacbd\uace0 - {}: {}ms", (Object)operationName, (Object)duration);
            } else if (duration > 300L) {
                log.info("\uc131\ub2a5 \uce21\uc815 - {}: {}ms", (Object)operationName, (Object)duration);
            } else {
                log.debug("\uc131\ub2a5 \uce21\uc815 - {}: {}ms", (Object)operationName, (Object)duration);
            }
            T t = result;
            return t;
        }
        catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put((String)"executionTime", (String)String.valueOf(duration));
            log.error("\uc2e4\ud589 \uc624\ub958 - {} ({}ms): {}", new Object[]{operationName, duration, e.getMessage()});
            throw e;
        }
        finally {
            MDC.remove((String)"executionTime");
        }
    }

    public static void debugObject(String label, Object object) {
        if (LoggingUtils.isProductionEnvironment()) {
            return;
        }
        try {
            if (object == null) {
                log.debug("{}: null", (Object)label);
                return;
            }
            if (object instanceof Map) {
                LoggingUtils.debugMap(label, (Map)object);
            } else if (object instanceof Collection) {
                LoggingUtils.debugCollection(label, (Collection)object);
            } else if (object.getClass().isArray()) {
                LoggingUtils.debugArray(label, object);
            } else {
                log.debug("{}: {} (\ud0c0\uc785: {})", new Object[]{label, object, object.getClass().getSimpleName()});
            }
        }
        catch (Exception e) {
            log.debug("{}: [\ub85c\uae45 \uc624\ub958: {}]", (Object)label, (Object)e.getMessage());
        }
    }

    private static void debugMap(String label, Map<?, ?> map) {
        if (map.isEmpty()) {
            log.debug("{}: \ube48 Map", (Object)label);
            return;
        }
        log.debug("{}:", (Object)label);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && LoggingUtils.isSensitiveKey(key.toString())) {
                log.debug("  {}: ******", key);
                continue;
            }
            log.debug("  {}: {}", key, value);
        }
    }

    private static void debugCollection(String label, Collection<?> collection) {
        if (collection.isEmpty()) {
            log.debug("{}: \ube48 \uceec\ub809\uc158", (Object)label);
            return;
        }
        log.debug("{}:", (Object)label);
        int index = 0;
        for (Object item : collection) {
            log.debug("  [{}]: {}", (Object)index++, item);
            if (index < 10) continue;
            log.debug("  ... \uc678 {}\uac1c \ud56d\ubaa9", (Object)(collection.size() - 10));
            break;
        }
    }

    private static void debugArray(String label, Object array) {
        if (array instanceof Object[]) {
            Object[] objArray = (Object[])array;
            if (objArray.length == 0) {
                log.debug("{}: \ube48 \ubc30\uc5f4", (Object)label);
                return;
            }
            log.debug("{}:", (Object)label);
            int limit = Math.min(objArray.length, 10);
            for (int i = 0; i < limit; ++i) {
                log.debug("  [{}]: {}", (Object)i, objArray[i]);
            }
            if (objArray.length > 10) {
                log.debug("  ... \uc678 {}\uac1c \ud56d\ubaa9", (Object)(objArray.length - 10));
            }
        } else {
            log.debug("{}: {}", (Object)label, array);
        }
    }

    public static String formatException(Throwable ex) {
        Throwable cause;
        if (ex == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            int count = 0;
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (!className.startsWith(APP_PACKAGE) && !className.contains("Controller") && !className.contains("Service") && !className.contains("Repository")) continue;
                sb.append("\tat ").append(className).append(".").append(element.getMethodName()).append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")\n");
                if (++count >= 8) break;
            }
            if (count == 0 && stackTrace.length > 0) {
                int limit = Math.min(3, stackTrace.length);
                for (int i = 0; i < limit; ++i) {
                    StackTraceElement element = stackTrace[i];
                    sb.append("\tat ").append(element.getClassName()).append(".").append(element.getMethodName()).append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")\n");
                }
            }
        }
        if ((cause = ex.getCause()) != null && cause != ex) {
            sb.append("Caused by: ").append(LoggingUtils.formatException(cause));
        }
        return sb.toString();
    }

    public static void logSql(String sql, Object ... params) {
        if (LoggingUtils.isProductionEnvironment()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SQL \ucffc\ub9ac:\n").append(sql).append("\n");
        if (params != null && params.length > 0) {
            sb.append("\ud30c\ub77c\ubbf8\ud130:");
            for (int i = 0; i < params.length; ++i) {
                sb.append("\n  ").append(i + 1).append(": ");
                if (params[i] == null) {
                    sb.append("null");
                    continue;
                }
                if (LoggingUtils.isSensitiveValue(params[i])) {
                    sb.append("******");
                    continue;
                }
                sb.append(params[i]).append(" (").append(params[i].getClass().getSimpleName()).append(")");
            }
        }
        log.debug(sb.toString());
    }

    public static void logRequestInfo(HttpServletRequest request) {
        if (LoggingUtils.isProductionEnvironment()) {
            log.info("\uc694\uccad: {} {}", (Object)request.getMethod(), (Object)request.getRequestURI());
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\uc694\uccad \uc815\ubcf4:\n");
        sb.append("  URI: ").append(request.getRequestURI()).append("\n");
        sb.append("  Method: ").append(request.getMethod()).append("\n");
        sb.append("  Query: ").append(request.getQueryString()).append("\n");
        sb.append("  Client IP: ").append(request.getRemoteAddr()).append("\n");
        sb.append("  User-Agent: ").append(request.getHeader("User-Agent")).append("\n");
        sb.append("  Headers:");
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            if (LoggingUtils.isSensitiveKey(headerName)) {
                sb.append("\n    ").append(headerName).append(": ******");
                continue;
            }
            sb.append("\n    ").append(headerName).append(": ").append(request.getHeader(headerName));
        }
        log.debug(sb.toString());
    }

    private static boolean isProductionEnvironment() {
        String profile = System.getProperty("spring.profiles.active");
        return "prod".equals(profile);
    }

    private static boolean isSensitiveKey(String key) {
        if (key == null) {
            return false;
        }
        String lowerKey = key.toLowerCase();
        return lowerKey.contains("password") || lowerKey.contains("secret") || lowerKey.contains("token") || lowerKey.contains("auth") || lowerKey.contains("key") || lowerKey.contains("credential");
    }

    private static boolean isSensitiveValue(Object value) {
        if (value == null) {
            return false;
        }
        if (!(value instanceof String)) {
            return false;
        }
        String strValue = (String)value;
        return strValue.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}") || strValue.matches("\\d{6}-\\d{7}") || strValue.length() >= 20;
    }

    public static String generateTransactionId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static void clearMdc() {
        MDC.clear();
    }

    public static Map<String, String> backupMdc() {
        return MDC.getCopyOfContextMap();
    }

    public static void restoreMdc(Map<String, String> contextMap) {
        MDC.clear();
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }
}

