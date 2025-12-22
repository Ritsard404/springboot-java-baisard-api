/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.stereotype.Component
 *  org.springframework.web.filter.OncePerRequestFilter
 *  org.springframework.web.util.ContentCachingRequestWrapper
 *  org.springframework.web.util.ContentCachingResponseWrapper
 */
package com.ritsard.baisard.utils.logging;

import com.lodong.utilsmodule.logging.v2.EnhancedLoggingUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class SimpleMdcFilter
extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SimpleMdcFilter.class);
    private static final String REQUEST_ID = "requestId";
    private static final String METHOD = "method";
    private static final String URI = "uri";
    private static final String CLIENT_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";
    private static final String DURATION = "duration";

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();
        try {
            this.setupMdc((HttpServletRequest)requestWrapper);
            String requestId = MDC.get((String)REQUEST_ID);
            if (requestId != null) {
                responseWrapper.setHeader("X-Request-ID", requestId);
                requestWrapper.setAttribute(REQUEST_ID, (Object)requestId);
            }
            filterChain.doFilter((ServletRequest)requestWrapper, (ServletResponse)responseWrapper);
            long duration = System.currentTimeMillis() - startTime;
            MDC.put((String)DURATION, (String)String.valueOf(duration));
            if (!this.isStaticResource(requestWrapper.getRequestURI())) {
                this.logRequestCompletion(requestWrapper, responseWrapper, duration);
            }
        }
        catch (Exception e) {
            String errorId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put((String)"errorId", (String)errorId);
            log.error("\ud83d\udd34 \uc694\uccad \ucc98\ub9ac \uc624\ub958 [ErrorID:{}]: {}", (Object)errorId, (Object)e.getMessage());
            EnhancedLoggingUtil.logJumpableStackTrace(e);
            throw e;
        }
        finally {
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void setupMdc(HttpServletRequest request) {
        String queryString;
        String username;
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }
        MDC.put((String)REQUEST_ID, (String)requestId);
        MDC.put((String)METHOD, (String)request.getMethod());
        MDC.put((String)URI, (String)request.getRequestURI());
        MDC.put((String)CLIENT_IP, (String)this.getClientIp(request));
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            MDC.put((String)USER_AGENT, (String)userAgent);
        }
        if ((username = request.getRemoteUser()) != null) {
            MDC.put((String)"username", (String)username);
        }
        if ((queryString = request.getQueryString()) != null && !queryString.isEmpty()) {
            MDC.put((String)"queryString", (String)queryString);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (this.isEmpty(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (this.isEmpty(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (this.isEmpty(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (this.isEmpty(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (this.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty() || "unknown".equalsIgnoreCase(str);
    }

    private boolean isStaticResource(String uri) {
        return uri.contains("/static/") || uri.contains("/assets/") || uri.contains("/public/") || uri.contains("/resources/") || uri.contains("/images/") || uri.contains("/css/") || uri.contains("/js/") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif") || uri.endsWith(".svg") || uri.endsWith(".woff") || uri.endsWith(".woff2") || uri.endsWith(".ttf");
    }

    private void logRequestCompletion(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String clientIp = this.getClientIp((HttpServletRequest)request);
        String requestId = MDC.get((String)REQUEST_ID);
        if (status >= 500) {
            log.error("\ud83d\udd34 HTTP {} {} | \uc0c1\ud0dc: {} | \uc694\uccadID: {} | {}ms | IP: {}", new Object[]{method, uri, status, requestId, duration, clientIp});
        } else if (status >= 400) {
            log.warn("\ud83d\udfe0 HTTP {} {} | \uc0c1\ud0dc: {} | \uc694\uccadID: {} | {}ms | IP: {}", new Object[]{method, uri, status, requestId, duration, clientIp});
        } else if (status >= 300) {
            log.info("\ud83d\udfe1 HTTP {} {} | \uc0c1\ud0dc: {} (\ub9ac\ub2e4\uc774\ub809\uc158) | \uc694\uccadID: {} | {}ms", new Object[]{method, uri, status, requestId, duration});
        } else if (log.isDebugEnabled()) {
            log.debug("\ud83d\udfe2 HTTP {} {} | \uc0c1\ud0dc: {} | \uc694\uccadID: {} | {}ms", new Object[]{method, uri, status, requestId, duration});
        }
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/health") || path.equals("/metrics") || path.startsWith("/actuator/");
    }
}

