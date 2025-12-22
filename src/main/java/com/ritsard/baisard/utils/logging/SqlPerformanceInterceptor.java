/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.resource.jdbc.spi.StatementInspector
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.stereotype.Component
 */
package com.ritsard.baisard.utils.logging;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SqlPerformanceInterceptor
implements StatementInspector {
    private static final Logger log = LoggerFactory.getLogger(SqlPerformanceInterceptor.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger((String)"com.lodong.performance");
    private final ThreadLocal<Map<String, Long>> queryStartTimes = ThreadLocal.withInitial(HashMap::new);
    private static final long SLOW_QUERY_THRESHOLD = 500L;

    public String inspect(String sql) {
        if (sql == null) {
            return null;
        }
        String queryHash = this.generateQueryHash(sql);
        this.queryStartTimes.get().put(queryHash, System.currentTimeMillis());
        if (this.isDevEnvironment()) {
            log.debug("\uc2e4\ud589 SQL: {}", (Object)this.formatSql(sql));
        }
        return sql;
    }

    public void afterQuery(String sql) {
        if (sql == null) {
            return;
        }
        String queryHash = this.generateQueryHash(sql);
        Map<String, Long> startTimes = this.queryStartTimes.get();
        Long startTime = startTimes.remove(queryHash);
        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            if (executionTime > 500L) {
                MDC.put((String)"executionTime", (String)String.valueOf(executionTime));
                performanceLogger.warn("\ub290\ub9b0 SQL \ucffc\ub9ac \uac10\uc9c0: {}ms\n{}", (Object)executionTime, (Object)this.formatSql(sql));
                MDC.remove((String)"executionTime");
            } else if (this.isDevEnvironment()) {
                performanceLogger.info("SQL \uc2e4\ud589 \uc2dc\uac04: {}ms", (Object)executionTime);
            }
        }
    }

    public void clearQueryStartTimes() {
        this.queryStartTimes.remove();
    }

    private String generateQueryHash(String sql) {
        return String.valueOf(sql.hashCode());
    }

    private String formatSql(String sql) {
        if (sql == null) {
            return "null";
        }
        String formattedSql = sql.replaceAll("\\s+", " ").replaceAll("\\( ", "(").replaceAll(" \\)", ")").trim();
        formattedSql = formattedSql.replaceAll("(?i)\\bselect\\b", "\nSELECT").replaceAll("(?i)\\bfrom\\b", "\nFROM").replaceAll("(?i)\\bwhere\\b", "\nWHERE").replaceAll("(?i)\\band\\b", "\n  AND").replaceAll("(?i)\\bor\\b", "\n  OR").replaceAll("(?i)\\bjoin\\b", "\nJOIN").replaceAll("(?i)\\bleft\\s+join\\b", "\nLEFT JOIN").replaceAll("(?i)\\bright\\s+join\\b", "\nRIGHT JOIN").replaceAll("(?i)\\binner\\s+join\\b", "\nINNER JOIN").replaceAll("(?i)\\bouter\\s+join\\b", "\nOUTER JOIN").replaceAll("(?i)\\bgroup\\s+by\\b", "\nGROUP BY").replaceAll("(?i)\\bhaving\\b", "\nHAVING").replaceAll("(?i)\\border\\s+by\\b", "\nORDER BY").replaceAll("(?i)\\blimit\\b", "\nLIMIT").replaceAll("(?i)\\boffset\\b", "\nOFFSET").replaceAll("(?i)\\bunion\\b", "\nUNION").replaceAll("(?i)\\binsert\\s+into\\b", "\nINSERT INTO").replaceAll("(?i)\\bupdate\\b", "\nUPDATE").replaceAll("(?i)\\bdelete\\s+from\\b", "\nDELETE FROM").replaceAll("(?i)\\bset\\b", "\nSET").replaceAll("(?i)\\bvalues\\b", "\nVALUES");
        return formattedSql;
    }

    private boolean isDevEnvironment() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return !"prod".equals(activeProfile);
    }
}

