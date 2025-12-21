package com.ritsard.baisard.jwt.config;


import com.ritsard.baisard.jwt.model.enums.JwtStrategyType;

public interface MemberProperties {
    default JwtStrategyType getStrategy() {
        return JwtStrategyType.JWS;
    }

    default String getSecretKey() {
        return "default-secret-key-internal-256-STILL-SMALL";
    }

    default long getAccessTokenExpireSeconds() {
        return 3600L;
    }

    default long getRefreshTokenExpireSeconds() {
        return 604800L;
    }
}
