package com.ritsard.baisard.utils.config;

import com.ritsard.baisard.utils.logging.EnhancedMdcFilter;
import com.ritsard.baisard.utils.logging.PerformanceLoggerAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {
    private static final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    @Bean
    public EnhancedMdcFilter enhancedMdcFilter() {
        // Translation: MDC Filter Registered: Adding trace ID and context info to all requests
        log.info("MDC Filter registered: Adding trace ID and context information to all requests");
        return new EnhancedMdcFilter();
    }

    @Bean
    public PerformanceLoggerAspect performanceLoggerAspect() {
        // Translation: Performance Logging Aspect Registered: Supporting @LogPerformance annotation
        log.info("Performance Logging Aspect registered: Supporting @LogPerformance annotation");
        return new PerformanceLoggerAspect();
    }

    @Bean
    @ConfigurationProperties(prefix = "logging.custom")
    public LoggingProperties loggingProperties() {
        return new LoggingProperties();
    }

    public static class LoggingProperties {
        private Performance performance = new Performance();
        private Masking masking = new Masking();
        private boolean compressLogs = true;
        private boolean logStaticResources = false;

        public Performance getPerformance() {
            return this.performance;
        }

        public void setPerformance(Performance performance) {
            this.performance = performance;
        }

        public Masking getMasking() {
            return this.masking;
        }

        public void setMasking(Masking masking) {
            this.masking = masking;
        }

        public boolean isCompressLogs() {
            return this.compressLogs;
        }

        public void setCompressLogs(boolean compressLogs) {
            this.compressLogs = compressLogs;
        }

        public boolean isLogStaticResources() {
            return this.logStaticResources;
        }

        public void setLogStaticResources(boolean logStaticResources) {
            this.logStaticResources = logStaticResources;
        }

        public static class Performance {
            private long defaultThreshold = 300L;
            private long databaseThreshold = 100L;
            private long apiCallThreshold = 500L;

            public long getDefaultThreshold() {
                return this.defaultThreshold;
            }

            public void setDefaultThreshold(long defaultThreshold) {
                this.defaultThreshold = defaultThreshold;
            }

            public long getDatabaseThreshold() {
                return this.databaseThreshold;
            }

            public void setDatabaseThreshold(long databaseThreshold) {
                this.databaseThreshold = databaseThreshold;
            }

            public long getApiCallThreshold() {
                return this.apiCallThreshold;
            }

            public void setApiCallThreshold(long apiCallThreshold) {
                this.apiCallThreshold = apiCallThreshold;
            }
        }

        public static class Masking {
            private boolean enabled = true;
            // Translated specific Korean PII fields: residentRegistrationNumber and phoneNumber
            private String[] fields = new String[]{"password", "residentRegistrationNumber", "credit_card", "cardNumber", "phoneNumber", "email"};

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String[] getFields() {
                return this.fields;
            }

            public void setFields(String[] fields) {
                this.fields = fields;
            }
        }
    }
}