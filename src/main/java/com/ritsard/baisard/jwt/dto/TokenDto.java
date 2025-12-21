package com.ritsard.baisard.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDto {
    protected String accessToken;
    protected String refreshToken;
    protected String loginType;

    // Custom factory method for 2 params
    public static TokenDto of(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Custom factory method for 3 params
    public static TokenDto of(String accessToken, String refreshToken, String loginType) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .loginType(loginType)
                .build();
    }
}