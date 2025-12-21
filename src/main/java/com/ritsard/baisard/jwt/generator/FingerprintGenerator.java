package com.ritsard.baisard.jwt.generator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface FingerprintGenerator {
    Map<String, Object> generate(HttpServletRequest request);

    String generateHash(HttpServletRequest request);
}
