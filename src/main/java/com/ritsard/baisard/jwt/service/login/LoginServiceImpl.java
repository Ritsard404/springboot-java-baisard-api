package com.ritsard.baisard.jwt.service.login;

import com.ritsard.baisard.jwt.config.MemberProperties;
import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.model.entity.Permission;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.repository.login.LoginCredentialRepository;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.helper.PasswordEncoderUtil;
import com.ritsard.baisard.utils.log.LoggingService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final LoginCredentialRepository loginCredentialRepository;
    private final BaseMemberRepository baseMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final LoggingService loggingService;
    private final MemberRedisService memberRedisService;
    private final MemberProperties config;

    @Override
    public LoginResponseDto login(LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        LoggingService var10000 = this.loggingService;
        String var10001 = dto.getIdentifier();
        var10000.logInfo("[로그인 요청] loginId=" + var10001 + ", loginType=" + String.valueOf(dto.getLoginType()));
        LoginCredential loginCredential = this.findValidMember(dto);
        BaseMember member = loginCredential.getMember();
        UUID memberUuid = member.getUuidMember();

        Set<String> roles = (Set) member.getPermissions().stream().map(Permission::getPermissionType).collect(Collectors.toSet());
        this.memberRedisService.save(member);

        String accessToken = this.tokenProvider.createAccessToken(memberUuid, roles, request);
        String refreshToken = this.tokenProvider.createRefreshToken(memberUuid, roles, request, response);
        this.applyTokensToResponse(accessToken, refreshToken, response);

        loggingService.logInfo("[Login Success] memberUuid=" + memberUuid);
        loggingService.logInfo("[Access Token issued] " + accessToken);
        loggingService.logInfo("[Refresh Token issued] " + refreshToken);

        return LoginResponseDto.builder()
                .uuidMember(memberUuid)
                .identifier(loginCredential.getIdentifier())
                .loginType(dto.getLoginType().name())
                .permissions(roles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private LoginCredential findValidMember(LoginRequestDto dto) {
        LoginCredential credential = (LoginCredential) this.loginCredentialRepository.findByIdentifier(dto.getIdentifier()).orElseThrow(() -> new NoSuchUserException("아이디 또는 비밀번호가 올바르지 않습니다."));
        if (credential.isDeleted()) {
            throw new NoSuchUserException("This user has been deleted\n.");
        } else if (!credential.getLoginType().equals(dto.getLoginType())) {
            throw new NoSuchUserException("Your ID or password is incorrect");
        } else if (!PasswordEncoderUtil.BCryptUtil.matches(dto.getPassword(), credential.getPassword())) {
            throw new NoSuchUserException("Your ID or password is incorrect.");
        } else {
            return credential;
        }
    }

    private void applyTokensToResponse(String accessToken, String refreshToken, HttpServletResponse response) {
        if (accessToken != null) {
            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("New-Access-Token", accessToken);
            response.setHeader("Access-Control-Expose-Headers", "Authorization, New-Access-Token");
            this.loggingService.logInfo("[Set response headers] Include access token");
        }

        if (refreshToken != null) {
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) this.config.getRefreshTokenExpireSeconds());
            response.addCookie(cookie);
            this.loggingService.logInfo("[Response Cookie Settings] Include refresh token");
        }

    }
}
