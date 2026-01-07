package com.ritsard.baisard.jwt.filter;


import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.vo.UserPrincipal;
import com.ritsard.baisard.utils.exceptions.JwtTokenExpiredException;
import com.ritsard.baisard.utils.exceptions.JwtTokenIsNotValid;
import com.ritsard.baisard.utils.log.LoggingService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final TokenProvider tokenProvider;
    private final MemberRedisService redisService;
    private final LoggingService loggingService;
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;
    @Value("${spring.application.name}")
    private String appName;
    private List<String> permitAllPaths = new ArrayList();
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private boolean isProduction() {
        return "prod".equalsIgnoreCase(this.activeProfile);
    }

    public void setPermitAllPaths(List<String> paths) {
        this.permitAllPaths = new ArrayList(paths);
        this.loggingService.logInfo("Setting up an authentication bypass path: " + String.valueOf(this.permitAllPaths));
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        this.loggingService.logDebug("request: " + method + " " + requestUri);
        if (this.isPermitAllPath(requestUri, method)) {
            this.loggingService.logDebug("Path without authentication required\n: " + requestUri);
            filterChain.doFilter(request, response);
        } else {
            try {
                this.authenticateRequest(request, response);
                filterChain.doFilter(request, response);
            } catch (JwtTokenExpiredException e) {
                log.warn("JWT Expired: {}", e.getMessage());
                this.sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            } catch (JwtTokenIsNotValid e) {
                log.warn("JWT Authentication failed\n: {}", e.getMessage());
                this.sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            } catch (Exception e) {
                log.error("An exception occurred during authentication processing.", e);
                this.sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred during authentication processing");
            }

        }
    }

    private void authenticateRequest(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = this.resolveAccessToken(request);
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                if (!this.tokenProvider.validateToken(accessToken, request)) {
                    throw new JwtTokenIsNotValid("Token verification failed");
                }

                UUID userId = this.tokenProvider.getUserId(accessToken);
                this.setAuthentication(userId, accessToken);
                this.loggingService.logInfo("Access token authentication successful\n: userId=" + String.valueOf(userId));
            } catch (JwtTokenExpiredException var5) {
                this.loggingService.logInfo("Access token expired, try with refresh token");
                this.handleRefreshToken(request, response);
            } catch (JwtTokenIsNotValid e) {
                this.loggingService.logWarn("Security risks\n: " + e.getMessage());
                throw new JwtTokenIsNotValid(e.getMessage());
            }

        } else {
            this.loggingService.logInfo("No access token");
            throw new JwtTokenIsNotValid("No access token");
        }
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = this.resolveRefreshToken(request);
        if (refreshToken != null && this.tokenProvider.validateToken(refreshToken, request)) {
            UUID memberUuid = this.tokenProvider.getUserId(refreshToken);
            List<String> authorities = this.tokenProvider.getAuthoritiesFromToken(refreshToken);
            Set<String> roles = new HashSet(authorities);
            String newAccessToken = this.tokenProvider.createAccessToken(memberUuid, roles, request);
            response.setHeader("Authorization", "Bearer " + newAccessToken);
            response.setHeader("New-Access-Token", newAccessToken);
            response.setHeader("Access-Control-Expose-Headers", "Authorization, New-Access-Token");
            this.setAuthentication(memberUuid, newAccessToken);
            this.loggingService.logInfo("[AccessToken Reissue successful] memberUuid=" + String.valueOf(memberUuid));
            this.loggingService.logInfo("[AccessToken Reissue successful] permission=" + String.valueOf(roles));
            this.loggingService.logInfo("[AccessToken Reissue successful] newAccessToken=" + newAccessToken);
        } else {
            this.loggingService.logWarn("[RefreshToken None or Expired] → Authentication Failed");
            throw new RuntimeException("The authentication token is invalid");
        }
    }

    private void setAuthentication(UUID userId, String accessToken) {
        List<GrantedAuthority> authorities = (List) this.tokenProvider.getAuthoritiesFromToken(accessToken).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        this.loggingService.logInfo("[setAuthentication] 인증 설정: userId=" + String.valueOf(userId));
        UserPrincipal userPrincipal = new UserPrincipal(userId, authorities, userId.toString());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, (Object) null, authorities));
        this.loggingService.logInfo("[확인] SecurityContext의 Authentication name: " + SecurityContextHolder.getContext().getAuthentication().getName());
        log.info("SecurityContext authorities: " +
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());

    }

    private String resolveAccessToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return bearer != null && bearer.startsWith("Bearer ") ? bearer.substring(7) : null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        return request.getCookies() == null
                ? null
                : Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            this.loggingService.logWarn("Response already committed: " + message);
        } else {
            SecurityContextHolder.clearContext();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, RefreshToken");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, New-Access-Token");
            response.sendError(status, message);
        }
    }

    private boolean isPermitAllPath(String uri, String method) {

        if ("OPTIONS".equals(method)) {
            return true;
        }

        // Ant-style path matching
        for (String pattern : permitAllPaths) {
            if (pathMatcher.match(pattern, uri)) {
                log.debug("Allowed path matching: pattern={}, URI={}", pattern, uri);
                return true;
            }
        }

        // Prefix-based matching
        if (permitAllPaths.stream().anyMatch(p -> uri.startsWith(p))) {
            return true;
        }

        // Swagger
        if (uri.startsWith("/swagger-ui/") || uri.startsWith("/v3/api-docs")) {
            return true;
        }

        // Auth (allow all except logout)
        String authBase = "/api/" + appName + "/auth";
        if (uri.startsWith(authBase) && !uri.startsWith(authBase + "/logout")) {
            return true;
        }

        // Static resources
        return uri.startsWith("/static/")
                || uri.startsWith("/assets/")
                || uri.startsWith("/favicon.ico")
                || uri.startsWith("/public/");
    }

    private String getClientIp(HttpServletRequest request) {
        if (!this.isProduction()) {
            this.loggingService.logDebug("Since it is a development environment, client IP verification is omitted");
            return "127.0.0.1";
        } else {
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getHeader("Proxy-Client-IP");
            }

            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getHeader("WL-Proxy-Client-IP");
            }

            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = request.getRemoteAddr();
            }

            int firstCommaIndex = clientIp.indexOf(44);
            if (firstCommaIndex != -1) {
                clientIp = clientIp.substring(0, firstCommaIndex);
            }

            return clientIp;
        }
    }

    @Generated
    public JwtAuthenticationFilter(final TokenProvider tokenProvider, final MemberRedisService redisService, final LoggingService loggingService) {
        this.tokenProvider = tokenProvider;
        this.redisService = redisService;
        this.loggingService = loggingService;
    }
}
