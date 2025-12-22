/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.stereotype.Component
 */
package com.ritsard.baisard.utils.logging.v2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
class SimplePerformanceLogger {
    private static final Logger performanceLogger = LoggerFactory.getLogger((String)"com.lodong.performance");
    private static final Logger log = LoggerFactory.getLogger(SimplePerformanceLogger.class);

    SimplePerformanceLogger() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="@annotation(com.lodong.utilsmodule.logging.LogPerformance)")
    public Object logMethodPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object;
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        LogPerformance annotation = signature.getMethod().getAnnotation(LogPerformance.class);
        long threshold = annotation.threshold();
        String description = annotation.description();
        String category = annotation.category();
        boolean logParams = annotation.logParams();
        boolean logResult = annotation.logResult();
        long startTime = System.currentTimeMillis();
        if (category != null && !category.isEmpty()) {
            MDC.put((String)"performanceCategory", (String)category);
        }
        if (logParams) {
            String params = this.formatParams(signature.getParameterNames(), joinPoint.getArgs());
            MDC.put((String)"methodParams", (String)params);
            log.debug("\u2699\ufe0f \uba54\uc18c\ub4dc \ud638\ucd9c: {}.{}({})", new Object[]{className, methodName, params});
        }
        Object result = null;
        try {
            object = result = joinPoint.proceed();
        }
        catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            MDC.put((String)"executionTime", (String)String.valueOf(executionTime));
            MDC.put((String)"className", (String)className);
            MDC.put((String)"methodName", (String)methodName);
            String logMessage = this.buildLogMessage(className, methodName, description, executionTime);
            if (logResult && result != null) {
                String resultStr = this.formatResult(result);
                log.debug("\ud83d\udd04 \uba54\uc18c\ub4dc \uacb0\uacfc: {}.{} = {}", new Object[]{className, methodName, resultStr});
            }
            if (executionTime > threshold) {
                performanceLogger.warn("\u26a0\ufe0f \uc131\ub2a5 \uacbd\uace0: {} (\uc784\uacc4\uac12: {} ms \ucd08\uacfc)", (Object)logMessage, (Object)threshold);
            } else {
                performanceLogger.info("\u2705 {}", (Object)logMessage);
            }
            MDC.remove((String)"executionTime");
            MDC.remove((String)"className");
            MDC.remove((String)"methodName");
            MDC.remove((String)"methodParams");
            if (category != null && !category.isEmpty()) {
                MDC.remove((String)"performanceCategory");
            }
            throw throwable;
        }
        long executionTime = System.currentTimeMillis() - startTime;
        MDC.put((String)"executionTime", (String)String.valueOf(executionTime));
        MDC.put((String)"className", (String)className);
        MDC.put((String)"methodName", (String)methodName);
        String logMessage = this.buildLogMessage(className, methodName, description, executionTime);
        if (logResult && result != null) {
            String resultStr = this.formatResult(result);
            log.debug("\ud83d\udd04 \uba54\uc18c\ub4dc \uacb0\uacfc: {}.{} = {}", new Object[]{className, methodName, resultStr});
        }
        if (executionTime > threshold) {
            performanceLogger.warn("\u26a0\ufe0f \uc131\ub2a5 \uacbd\uace0: {} (\uc784\uacc4\uac12: {} ms \ucd08\uacfc)", (Object)logMessage, (Object)threshold);
        } else {
            performanceLogger.info("\u2705 {}", (Object)logMessage);
        }
        MDC.remove((String)"executionTime");
        MDC.remove((String)"className");
        MDC.remove((String)"methodName");
        MDC.remove((String)"methodParams");
        if (category != null && !category.isEmpty()) {
            MDC.remove((String)"performanceCategory");
        }
        return object;
    }

    private String buildLogMessage(String className, String methodName, String description, long executionTime) {
        if (description != null && !description.isEmpty()) {
            return String.format("%s (%s.%s) \uc2e4\ud589 \uc2dc\uac04: %d ms", description, className, methodName, executionTime);
        }
        return String.format("%s.%s() \uc2e4\ud589 \uc2dc\uac04: %d ms", className, methodName, executionTime);
    }

    private String formatParams(String[] paramNames, Object[] paramValues) {
        if (paramNames == null || paramValues == null || paramNames.length == 0 || paramValues.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(paramNames.length, paramValues.length); ++i) {
            if (i > 0) {
                result.append(", ");
            }
            String paramName = paramNames[i];
            Object paramValue = paramValues[i];
            if (this.isSensitiveParam(paramName)) {
                result.append(paramName).append("=******");
                continue;
            }
            result.append(paramName).append("=");
            if (paramValue == null) {
                result.append("null");
                continue;
            }
            if (paramValue.getClass().isArray()) {
                result.append(this.formatArray(paramValue));
                continue;
            }
            result.append(this.truncateIfString(paramValue.toString()));
        }
        return result.toString();
    }

    private String formatArray(Object array) {
        if (array instanceof Object[]) {
            return Arrays.stream((Object[])array).map(Object::toString).map(this::truncateIfString).collect(Collectors.joining(", ", "[", "]"));
        }
        if (array instanceof int[]) {
            return Arrays.toString((int[])array);
        }
        if (array instanceof long[]) {
            return Arrays.toString((long[])array);
        }
        if (array instanceof double[]) {
            return Arrays.toString((double[])array);
        }
        if (array instanceof boolean[]) {
            return Arrays.toString((boolean[])array);
        }
        return array.toString();
    }

    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        if (result.getClass().isArray()) {
            return this.formatArray(result);
        }
        return this.truncateIfString(result.toString());
    }

    private String truncateIfString(String str) {
        if (str != null && str.length() > 100) {
            return str.substring(0, 97) + "...";
        }
        return str;
    }

    private boolean isSensitiveParam(String paramName) {
        if (paramName == null) {
            return false;
        }
        String lowerName = paramName.toLowerCase();
        return lowerName.contains("password") || lowerName.contains("token") || lowerName.contains("key") || lowerName.contains("secret") || lowerName.contains("credential") || lowerName.contains("auth");
    }
}

