package com.ritsard.baisard.global.config;

import com.ritsard.baisard.jwt.config.MemberProperties;
import com.ritsard.baisard.jwt.model.enums.JwtStrategyType;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberConfig implements MemberProperties {
    @Override
    public String getSecretKey() {
        return "your-super-secure-256bit-key-change-me";
    }

    @Override
    public long getAccessTokenExpireSeconds() {
        return 1800; // 30 minutes
    }

    @Override
    public long getRefreshTokenExpireSeconds() {
        return 60 * 60 * 24 * 14; // 14 days
    }


    @Override
    public JwtStrategyType getStrategy() {
        return JwtStrategyType.JWS;
    }
}
