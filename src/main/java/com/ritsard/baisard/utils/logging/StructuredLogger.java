/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 */
package com.ritsard.baisard.utils.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StructuredLogger {
    private final Logger logger;

    public static StructuredLogger getLogger(Class<?> clazz) {
        return new StructuredLogger(LoggerFactory.getLogger(clazz));
    }

    public static StructuredLogger getLogger(String name) {
        return new StructuredLogger(LoggerFactory.getLogger((String)name));
    }

    private StructuredLogger(Logger logger) {
        this.logger = logger;
    }

    public void trace(String message, Object ... args) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(message, args);
        }
    }

    public void debug(String message, Object ... args) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(message, args);
        }
    }

    public void info(String message, Object ... args) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info(message, args);
        }
    }

    public void warn(String message, Object ... args) {
        if (this.logger.isWarnEnabled()) {
            this.logger.warn(message, args);
        }
    }

    public void error(String message, Object ... args) {
        if (this.logger.isErrorEnabled()) {
            this.logger.error(message, args);
        }
    }

    public void error(String message, Throwable throwable, Object ... args) {
        if (this.logger.isErrorEnabled()) {
            String errorId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put((String)"errorId", (String)errorId);
            this.logger.error("\uc624\ub958 [ErrorID:{}]: " + message, new Object[]{errorId, args, throwable});
            MDC.remove((String)"errorId");
        }
    }

    public ContextBuilder withContext() {
        return new ContextBuilder(this);
    }

    public static class ContextBuilder {
        private final StructuredLogger logger;
        private final Map<String, String> contextMap = new HashMap<String, String>();

        private ContextBuilder(StructuredLogger logger) {
            this.logger = logger;
        }

        public ContextBuilder add(String key, String value) {
            this.contextMap.put(key, value);
            return this;
        }

        public void execute(Runnable logOperation) {
            try {
                this.contextMap.forEach(MDC::put);
                logOperation.run();
            }
            finally {
                this.contextMap.keySet().forEach(MDC::remove);
            }
        }

        public void trace(String message, Object ... args) {
            this.execute(() -> this.logger.trace(message, args));
        }

        public void debug(String message, Object ... args) {
            this.execute(() -> this.logger.debug(message, args));
        }

        public void info(String message, Object ... args) {
            this.execute(() -> this.logger.info(message, args));
        }

        public void warn(String message, Object ... args) {
            this.execute(() -> this.logger.warn(message, args));
        }

        public void error(String message, Object ... args) {
            this.execute(() -> this.logger.error(message, args));
        }

        public void error(String message, Throwable throwable, Object ... args) {
            this.execute(() -> this.logger.error(message, throwable, args));
        }
    }
}

