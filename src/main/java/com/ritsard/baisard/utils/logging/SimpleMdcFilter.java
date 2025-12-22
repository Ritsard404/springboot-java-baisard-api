package com.ritsard.baisard.utils.logging;

import com.ritsard.baisard.utils.logging.v2.EnhancedLoggingUtil;
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
public class SimpleMdcFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SimpleMdcFilter.class);
    private static final String REQUEST_ID = "requestId";
    private static final String METHOD = "method";
    private static final String URI = "uri";
    private static final String CLIENT_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";
    private static final String DURATION = "duration";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 50);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();

        try {
            setupMdc(requestWrapper);
            String requestId = MDC.get(REQUEST_ID);
            if (requestId != null) {
                responseWrapper.setHeader("X-Request-ID", requestId);
                requestWrapper.setAttribute(REQUEST_ID, requestId);
            }

            filterChain.doFilter((ServletRequest) requestWrapper, (ServletResponse) responseWrapper);

            long duration = System.currentTimeMillis() - startTime;
            MDC.put(DURATION, String.valueOf(duration));

            if (!isStaticResource(requestWrapper.getRequestURI())) {
                logRequestCompletion(requestWrapper, responseWrapper, duration);
            }

        } catch (Exception e) {
            String errorId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("errorId", errorId);
            log.error("🔴 Error handling exception [ErrorID:{}]: {}", errorId, e.getMessage());
            EnhancedLoggingUtil.logJumpableStackTrace(e);
            throw e;
        } finally {
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void setupMdc(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(REQUEST_ID, requestId);
        MDC.put(METHOD, request.getMethod());
        MDC.put(URI, request.getRequestURI());
        MDC.put(CLIENT_IP, getClientIp(request));

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            MDC.put(USER_AGENT, userAgent);
        }

        String username = request.getRemoteUser();
        if (username != null) {
            MDC.put("username", username);
        }

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            MDC.put("queryString", queryString);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isEmpty(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (isEmpty(ip)) ip = request.getHeader("WL-Proxy-Client-IP");
        if (isEmpty(ip)) ip = request.getHeader("HTTP_CLIENT_IP");
        if (isEmpty(ip)) ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isEmpty(ip)) ip = request.getRemoteAddr();

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty() || "unknown".equalsIgnoreCase(str);
    }

    private boolean isStaticResource(String uri) {
        return uri.contains("/static/") || uri.contains("/assets/") || uri.contains("/public/") ||
                uri.contains("/resources/") || uri.contains("/images/") || uri.contains("/css/") ||
                uri.contains("/js/") || uri.endsWith(".css") || uri.endsWith(".js") ||
                uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg") ||
                uri.endsWith(".jpeg") || uri.endsWith(".gif") || uri.endsWith(".svg") ||
                uri.endsWith(".woff") || uri.endsWith(".woff2") || uri.endsWith(".ttf");
    }

    private void logRequestCompletion(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);
        String requestId = MDC.get(REQUEST_ID);

        if (status >= 500) {
            log.error("🔴 HTTP {} {} | Status: {} | RequestID: {} | {}ms | IP: {}", method, uri, status, requestId, duration, clientIp);
        } else if (status >= 400) {
            log.warn("🟠 HTTP {} {} | Status: {} | RequestID: {} | {}ms | IP: {}", method, uri, status, requestId, duration, clientIp);
        } else if (status >= 300) {
            log.info("🟡 HTTP {} {} | Status: {} (Redirection) | RequestID: {} | {}ms", method, uri, status, requestId, duration);
        } else if (log.isDebugEnabled()) {
            log.debug("🟢 HTTP {} {} | Status: {} | RequestID: {} | {}ms", method, uri, status, requestId, duration);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/health") || path.equals("/metrics") || path.startsWith("/actuator/");
    }
}
