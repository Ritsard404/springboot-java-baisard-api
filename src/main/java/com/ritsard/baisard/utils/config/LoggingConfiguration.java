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
        log.info("MDC \ud544\ud130 \ub4f1\ub85d: \ubaa8\ub4e0 \uc694\uccad\uc5d0 \ucd94\uc801 ID \ubc0f \ucee8\ud14d\uc2a4\ud2b8 \uc815\ubcf4 \ucd94\uac00");
        return new EnhancedMdcFilter();
    }

    @Bean
    public PerformanceLoggerAspect performanceLoggerAspect() {
        log.info("\uc131\ub2a5 \ub85c\uae45 Aspect \ub4f1\ub85d: @LogPerformance \uc5b4\ub178\ud14c\uc774\uc158 \uc9c0\uc6d0");
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
            private String[] fields = new String[]{"password", "\uc8fc\ubbfc\ub4f1\ub85d\ubc88\ud638", "credit_card", "cardNumber", "\uc804\ud654\ubc88\ud638", "phoneNumber", "email"};

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

