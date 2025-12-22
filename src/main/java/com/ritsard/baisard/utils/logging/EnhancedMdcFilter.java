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
 * 이 필터는 모든 HTTP 요청에 대해 MDC(Mapped Diagnostic Context)에
 * 요청 추적 ID와 관련 정보를 추가합니다.
 * 로그 메시지에 자동으로 컨텍스트 정보가 포함되어 디버깅과 추적이 용이해집니다.
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

        // 요청과 응답을 캐싱하기 위해 래핑
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 50);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // 요청 시작 시 MDC 값 설정
            setupMdc(requestWrapper);

            // 응답 헤더에 요청 ID 추가 (클라이언트 추적용)
            String requestId = MDC.get(REQUEST_ID);
            if (requestId != null) {
                responseWrapper.setHeader("X-Request-ID", requestId);
            }

            // 시간 측정 시작
            long startTime = System.currentTimeMillis();

            // 요청 처리
            filterChain.doFilter(requestWrapper, responseWrapper);

            // 응답 시간 계산 및 MDC에 추가
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));

            // 정적 리소스가 아닌 경우에만 요청/응답 세부 정보 로깅
            if (!isStaticResource(requestWrapper.getRequestURI())) {
                logRequestDetails(requestWrapper, responseWrapper, duration);
            }

        } catch (Exception e) {
            // 에러 ID를 MDC에 추가
            String errorId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("errorId", errorId);
            log.error("요청 처리 오류 [ErrorID:{}]: {}", errorId, e.getMessage(), e);
            throw e;
        } finally {
            // 항상 응답 내용을 복사
            responseWrapper.copyBodyToResponse();
            // MDC 정리하여 메모리 누수 방지
            MDC.clear();
        }
    }

    private void setupMdc(HttpServletRequest request) {
        // 요청 ID 생성 또는 기존 ID 사용
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }

        // MDC에 요청 정보 추가
        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());
        MDC.put(REQUEST_IP, getClientIp(request));

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            MDC.put(USER_AGENT, userAgent);
        }

        // 사용자 인증 정보가 있는 경우 추가
        String username = request.getRemoteUser();
        if (username != null) {
            MDC.put("username", username);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 여러 프록시를 통과한 경우 첫 번째 IP만 반환
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

        // 요청 메소드, URI, 상태 코드, 처리 시간 등 로깅
        if (status >= 400) {
            // 에러 상태 코드인 경우 경고 로그
            log.warn("HTTP {} {} | 상태: {} | 처리시간: {} ms | 클라이언트: {} | 사용자 에이전트: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration,
                    getClientIp(request),
                    request.getHeader("User-Agent"));
        } else if (status >= 300) {
            // 리다이렉션 상태 코드인 경우
            log.info("HTTP {} {} | 상태: {} (리다이렉션) | 처리시간: {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration);
        } else {
            // 정상 상태 코드인 경우
            if (log.isDebugEnabled()) {
                log.debug("HTTP {} {} | 상태: {} | 처리시간: {} ms",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        duration);
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 특정 URI 패턴을 필터링에서 제외하려면 여기에 추가
        String path = request.getRequestURI();
        return path.equals("/health") ||
                path.equals("/metrics") ||
                path.startsWith("/actuator/");
    }
}