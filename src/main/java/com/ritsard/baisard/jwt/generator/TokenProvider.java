package com.ritsard.baisard.jwt.generator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TokenProvider {
    String createAccessToken(UUID userId, Set<String> permissions, HttpServletRequest request);

    String createRefreshToken(UUID userId, Set<String> permissions, HttpServletRequest request, HttpServletResponse response);

    boolean validateToken(String token, HttpServletRequest request);

    UUID getUserId(String token);

    void deleteRefreshTokenCookie(HttpServletResponse response);

    List<String> getAuthoritiesFromToken(String token);
}
