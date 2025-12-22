/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.ritsard.baisard.utils.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingService {
    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    public void logInfo(String message) {
        StackTraceElement stackTraceElement = this.getCallingStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();
        log.info("{}({}:{})", new Object[]{className, fileName, lineNumber});
        log.info("\u2514\u2500 Method: {}, Message: {}", (Object)methodName, (Object)message);
    }

    public void logMethodCheck() {
        StackTraceElement stackTraceElement = this.getCallingStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();
        log.trace("Method called: {}({}:{})", new Object[]{className, fileName, lineNumber});
        log.trace("\u2514\u2500 Method: {}", (Object)methodName);
    }

    public void logError(String message) {
        StackTraceElement stackTraceElement = this.getCallingStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();
        log.error("ERROR at {}({}:{})", new Object[]{className, fileName, lineNumber});
        log.error("\u2514\u2500 Method: {}, Message: {}", (Object)methodName, (Object)message);
    }

    public void logError(String message, Throwable throwable) {
        StackTraceElement stackTraceElement = this.getCallingStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();
        log.error("ERROR at {}({}:{})", new Object[]{className, fileName, lineNumber});
        log.error("\u2514\u2500 Method: {}, Message: {}", (Object)methodName, (Object)message);
        if (throwable != null) {
            log.error("\u2514\u2500 Exception: {}", (Object)throwable.getClass().getName());
            log.error("\u2514\u2500 Exception message: {}", (Object)throwable.getMessage());
            if (throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
                StackTraceElement errorElement = throwable.getStackTrace()[0];
                log.error("\u2514\u2500 Exception location: {}({}:{})", new Object[]{errorElement.getClassName(), errorElement.getFileName(), errorElement.getLineNumber()});
            }
            this.logTopStackTraces(throwable, 3);
        }
    }

    public void logDebug(String message) {
        StackTraceElement stackTraceElement = this.getCallingStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();
        log.debug("{}({}:{})", new Object[]{className, fileName, lineNumber});
        log.debug("\u2514\u2500 Method: {}, Message: {}", (Object)methodName, (Object)message);
    }

    public void logWarn(String message) {
        StackTraceElement stackTraceElement = this.getCallingStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();
        log.warn("WARN at {}({}:{})", new Object[]{className, fileName, lineNumber});
        log.warn("\u2514\u2500 Method: {}, Message: {}", (Object)methodName, (Object)message);
    }

    private void logTopStackTraces(Throwable throwable, int maxEntries) {
        if (throwable == null || throwable.getStackTrace() == null) {
            return;
        }
        StackTraceElement[] traces = throwable.getStackTrace();
        int loopCount = Math.min(traces.length, maxEntries);
        if (loopCount > 0) {
            log.error("\u2514\u2500 Stack trace:");
            for (int i = 0; i < loopCount; ++i) {
                StackTraceElement trace = traces[i];
                log.error("    {}. {}({}:{})", new Object[]{i + 1, trace.getClassName() + "." + trace.getMethodName(), trace.getFileName(), trace.getLineNumber()});
            }
        }
    }

    private StackTraceElement getCallingStackTraceElement() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3];
    }
}

