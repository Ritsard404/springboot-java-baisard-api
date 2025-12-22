package com.ritsard.baisard.utils.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

/**
 * Enhanced MDC Inserting Filter
 * <p>
 * This filter adds a request tracking ID and related information to the MDC
 * for all HTTP requests. Log messages will automatically include context
 * information, making debugging and tracing easier.
 */
@Component
public class EnhancedMdcFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(EnhancedMdcFilter.class);

    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_METHOD = "method";
    private static final String REQUEST_URI = "uri";
    private static final String REQUEST_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap request and response for caching
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 50);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // Set MDC values at request start
            setupMdc(requestWrapper);

            // Add request ID to response header for client tracking
            String requestId = MDC.get(REQUEST_ID);
            if (requestId != null) {
                responseWrapper.setHeader("X-Request-ID", requestId);
            }

            // Start timing
            long startTime = System.currentTimeMillis();

            // Process request
            filterChain.doFilter(requestWrapper, responseWrapper);

            // Calculate duration and add to MDC
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));

            // Log details only for non-static resources
            if (!isStaticResource(requestWrapper.getRequestURI())) {
                logRequestDetails(requestWrapper, responseWrapper, duration);
            }

        } catch (Exception e) {
            // Add error ID to MDC
            String errorId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("errorId", errorId);
            log.error("Request handling error [ErrorID:{}]: {}", errorId, e.getMessage(), e);
            throw e;
        } finally {
            // Always copy response body
            responseWrapper.copyBodyToResponse();
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }

    private void setupMdc(HttpServletRequest request) {
        // Generate or reuse request ID
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }

        // Add request info to MDC
        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());
        MDC.put(REQUEST_IP, getClientIp(request));

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            MDC.put(USER_AGENT, userAgent);
        }

        // Add username if authenticated
        String username = request.getRemoteUser();
        if (username != null) {
            MDC.put("username", username);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();

        // If multiple IPs, return the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private boolean isStaticResource(String uri) {
        return uri.contains("/static/") ||
                uri.contains("/assets/") ||
                uri.contains("/public/") ||
                uri.contains("/resources/") ||
                uri.contains("/images/") ||
                uri.contains("/css/") ||
                uri.contains("/js/") ||
                uri.endsWith(".css") ||
                uri.endsWith(".js") ||
                uri.endsWith(".ico") ||
                uri.endsWith(".png") ||
                uri.endsWith(".jpg") ||
                uri.endsWith(".jpeg") ||
                uri.endsWith(".gif") ||
                uri.endsWith(".svg") ||
                uri.endsWith(".woff") ||
                uri.endsWith(".woff2") ||
                uri.endsWith(".ttf");
    }

    private void logRequestDetails(ContentCachingRequestWrapper request,
                                   ContentCachingResponseWrapper response,
                                   long duration) {

        int status = response.getStatus();

        // Log method, URI, status code, duration, client IP, and user agent
        if (status >= 400) {
            log.warn("HTTP {} {} | Status: {} | Duration: {} ms | Client: {} | User-Agent: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration,
                    getClientIp(request),
                    request.getHeader("User-Agent"));
        } else if (status >= 300) {
            log.info("HTTP {} {} | Status: {} (Redirection) | Duration: {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("HTTP {} {} | Status: {} | Duration: {} ms",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        duration);
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Exclude specific URI patterns from the filter
        String path = request.getRequestURI();
        return path.equals("/health") ||
                path.equals("/metrics") ||
                path.startsWith("/actuator/");
    }
}
