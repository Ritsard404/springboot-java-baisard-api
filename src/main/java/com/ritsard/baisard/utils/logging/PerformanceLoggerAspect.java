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
 *  org.springframework.util.StopWatch
 */
package com.ritsard.baisard.utils.logging;

import com.lodong.utilsmodule.logging.LogPerformance;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Aspect
@Component
public class PerformanceLoggerAspect {
    private static final Logger performanceLogger = LoggerFactory.getLogger((String)"com.lodong.performance");
    private static final Logger log = LoggerFactory.getLogger(PerformanceLoggerAspect.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="@annotation(com.lodong.utilsmodule.logging.LogPerformance)")
    public Object logMethodPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String logMessage;
        Object object;
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        LogPerformance annotation = method.getAnnotation(LogPerformance.class);
        long threshold = annotation.threshold();
        String description = annotation.description();
        String category = annotation.category();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (category != null && !category.isEmpty()) {
            MDC.put((String)"performanceCategory", (String)category);
        }
        try {
            object = joinPoint.proceed();
        }
        catch (Throwable throwable) {
            String logMessage2;
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            MDC.put((String)"executionTime", (String)String.valueOf(executionTime));
            MDC.put((String)"className", (String)className);
            MDC.put((String)"methodName", (String)methodName);
            String string = logMessage2 = description.isEmpty() ? String.format("%s.%s() \uc2e4\ud589 \uc2dc\uac04: %d ms", className, methodName, executionTime) : String.format("%s (%s.%s) \uc2e4\ud589 \uc2dc\uac04: %d ms", description, className, methodName, executionTime);
            if (executionTime > threshold) {
                performanceLogger.warn("\uc131\ub2a5 \uacbd\uace0: {} (\uc784\uacc4\uac12: {} ms \ucd08\uacfc)", (Object)logMessage2, (Object)threshold);
            } else {
                performanceLogger.info(logMessage2);
            }
            MDC.remove((String)"executionTime");
            MDC.remove((String)"className");
            MDC.remove((String)"methodName");
            if (category != null && !category.isEmpty()) {
                MDC.remove((String)"performanceCategory");
            }
            throw throwable;
        }
        stopWatch.stop();
        long executionTime = stopWatch.getTotalTimeMillis();
        MDC.put((String)"executionTime", (String)String.valueOf(executionTime));
        MDC.put((String)"className", (String)className);
        MDC.put((String)"methodName", (String)methodName);
        String string = logMessage = description.isEmpty() ? String.format("%s.%s() \uc2e4\ud589 \uc2dc\uac04: %d ms", className, methodName, executionTime) : String.format("%s (%s.%s) \uc2e4\ud589 \uc2dc\uac04: %d ms", description, className, methodName, executionTime);
        if (executionTime > threshold) {
            performanceLogger.warn("\uc131\ub2a5 \uacbd\uace0: {} (\uc784\uacc4\uac12: {} ms \ucd08\uacfc)", (Object)logMessage, (Object)threshold);
        } else {
            performanceLogger.info(logMessage);
        }
        MDC.remove((String)"executionTime");
        MDC.remove((String)"className");
        MDC.remove((String)"methodName");
        if (category != null && !category.isEmpty()) {
            MDC.remove((String)"performanceCategory");
        }
        return object;
    }
}

