package com.ritsard.baisard.utils.log;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Enhanced logging service
 *
 * Improves the existing logging service to increase readability
 * and outputs logs in a format that allows direct navigation
 * to the source code from the IDE.
 */
@Component
@RequiredArgsConstructor
public class LoggingService {
    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    /**
     * Records INFO logs in a format that allows clicking
     * and jumping directly to the source code in the IDE.
     *
     * @param message log message
     */
    public void logInfo(String message) {
        StackTraceElement stackTraceElement = getCallingStackTraceElement();

        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();

        // Separate message and location information for better readability
        log.info("{}({}:{})", className, fileName, lineNumber);
        log.info("└─ Method: {}, Message: {}", methodName, message);
    }

    /**
     * TRACE log for method call tracking
     */
    public void logMethodCheck() {
        StackTraceElement stackTraceElement = getCallingStackTraceElement();

        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();

        // Clickable format in the IDE
        log.trace("Method called: {}({}:{})", className, fileName, lineNumber);
        log.trace("└─ Method: {}", methodName);
    }

    /**
     * Logs only an error message and allows
     * clicking in the IDE to navigate to the source code.
     *
     * @param message error message to log
     */
    public void logError(String message) {
        StackTraceElement stackTraceElement = getCallingStackTraceElement();

        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();

        // Separate message and location information; clickable in the IDE
        log.error("ERROR at {}({}:{})", className, fileName, lineNumber);
        log.error("└─ Method: {}, Message: {}", methodName, message);
    }

    /**
     * Logs an error message together with an exception
     * and allows clicking in the IDE to navigate to the source code.
     *
     * @param message error message to log
     * @param throwable exception to log
     */
    public void logError(String message, Throwable throwable) {
        StackTraceElement stackTraceElement = getCallingStackTraceElement();

        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();

        // Main error location – clickable in the IDE
        log.error("ERROR at {}({}:{})", className, fileName, lineNumber);
        log.error("└─ Method: {}, Message: {}", methodName, message);

        // Log exception information
        if (throwable != null) {
            log.error("└─ Exception: {}", throwable.getClass().getName());
            log.error("└─ Exception message: {}", throwable.getMessage());

            // First element of the exception stack trace (direct error location)
            if (throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
                StackTraceElement errorElement = throwable.getStackTrace()[0];
                log.error("└─ Exception location: {}({}:{})",
                        errorElement.getClassName(),
                        errorElement.getFileName(),
                        errorElement.getLineNumber());
            }

            // Key stack trace entries (up to 3)
            logTopStackTraces(throwable, 3);
        }
    }

    /**
     * Outputs debug logs in a format that allows
     * clicking and navigating in the IDE.
     *
     * @param message debug message to log
     */
    public void logDebug(String message) {
        StackTraceElement stackTraceElement = getCallingStackTraceElement();

        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();

        log.debug("{}({}:{})", className, fileName, lineNumber);
        log.debug("└─ Method: {}, Message: {}", methodName, message);
    }

    /**
     * Outputs warning logs in a format that allows
     * clicking and navigating in the IDE.
     *
     * @param message warning message to log
     */
    public void logWarn(String message) {
        StackTraceElement stackTraceElement = getCallingStackTraceElement();

        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        int lineNumber = stackTraceElement.getLineNumber();
        String fileName = stackTraceElement.getFileName();

        log.warn("WARN at {}({}:{})", className, fileName, lineNumber);
        log.warn("└─ Method: {}, Message: {}", methodName, message);
    }

    /**
     * Logs key stack trace entries (up to maxEntries).
     *
     * @param throwable exception object
     * @param maxEntries maximum number of stack trace entries to output
     */
    private void logTopStackTraces(Throwable throwable, int maxEntries) {
        if (throwable == null || throwable.getStackTrace() == null) {
            return;
        }

        StackTraceElement[] traces = throwable.getStackTrace();
        int loopCount = Math.min(traces.length, maxEntries);

        if (loopCount > 0) {
            log.error("└─ Stack trace:");
            for (int i = 0; i < loopCount; i++) {
                StackTraceElement trace = traces[i];
                log.error("    {}. {}({}:{})",
                        i + 1,
                        trace.getClassName() + "." + trace.getMethodName(),
                        trace.getFileName(),
                        trace.getLineNumber());
            }
        }
    }

    /**
     * Retrieves the actual caller location information
     * from the call stack.
     *
     * @return stack trace element of the caller
     */
    private StackTraceElement getCallingStackTraceElement() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 0: getStackTrace
        // 1: getCallingStackTraceElement
        // 2: logXXX
        // 3: actual caller
        return stackTrace[3];
    }
}
