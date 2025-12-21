package com.ritsard.baisard.jwt.generator;

import com.ritsard.baisard.jwt.config.MemberProperties;
import com.ritsard.baisard.jwt.model.enums.JwtStrategyType;
import com.ritsard.baisard.jwt.utils.DeviceInfoManager;
import com.ritsard.baisard.utils.exceptions.JwtTokenExpiredException;
import com.ritsard.baisard.utils.log.LoggingService;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class TokenProviderImpl implements TokenProvider {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TokenProviderImpl.class);
    private final MemberProperties config;
    private final FingerprintGenerator fingerprintGenerator;
    private final LoggingService loggingService;
    private final DeviceInfoManager deviceInfoManager;
    private Key jwsKey;
    private SecretKey jweKey;
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    private boolean isProduction() {
        return "prod".equalsIgnoreCase(this.activeProfile);
    }

    @PostConstruct
    public void init() {
        this.initializeKeys(this.config.getSecretKey());
    }

    public void initializeKeys(String secretKey) {
        if (this.config.getStrategy() == JwtStrategyType.JWS) {
            this.jwsKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        } else {
            byte[] decodedKey = Base64.getUrlDecoder().decode(secretKey);
            this.jweKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        }

    }

    public String createAccessToken(UUID userId, Set<String> permissions, HttpServletRequest request) {
        Map<String, Object> deviceInfo = this.deviceInfoManager.collectDeviceInfo(request);
        String deviceHash = this.deviceInfoManager.createDeviceHash(deviceInfo);
        Map<String, Object> fingerprint = this.fingerprintGenerator.generate(request);
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.config.getAccessTokenExpireSeconds() * 1000L);
        return this.config.getStrategy() == JwtStrategyType.JWS ? this.createJwsToken(userId, permissions, deviceInfo, deviceHash, fingerprint, jti, now, expiryDate) : this.createJweToken(userId, permissions, deviceInfo, deviceHash, fingerprint, jti, now, expiryDate);
    }

    private String createJwsToken(UUID userId, Set<String> permissions, Map<String, Object> deviceInfo, String deviceHash, Map<String, Object> fingerprint, String jti, Date issuedAt, Date expiryDate) {
        Map<String, Object> claims = new HashMap();
        claims.put("sub", userId.toString());
        claims.put("perm", permissions);
        claims.put("fingerprint", fingerprint);
        claims.put("jti", jti);
        claims.put("deviceHash", deviceHash);
        claims.put("deviceInfo", deviceInfo);
        return Jwts.builder().setClaims(claims).setIssuedAt(issuedAt).setExpiration(expiryDate).signWith(this.jwsKey, SignatureAlgorithm.HS256).compact();
    }

    private String createJweToken(UUID userId, Set<String> permissions, Map<String, Object> deviceInfo, String deviceHash, Map<String, Object> fingerprint, String jti, Date issuedAt, Date expiryDate) {
        try {
            JWTClaimsSet claimsSet = (new JWTClaimsSet.Builder()).subject(userId.toString()).issueTime(issuedAt).expirationTime(expiryDate).claim("perm", permissions).claim("fingerprint", fingerprint).claim("jti", jti).claim("deviceHash", deviceHash).claim("deviceInfo", deviceInfo).build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            JWEObject jweObject = new JWEObject(new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM), new Payload(signedJWT));
            jweObject.encrypt(new DirectEncrypter(this.jweKey));
            return jweObject.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWE 생성 오류", e);
        }
    }

    public void debugMessage(String message, String value) {
        log.debug("TokenProviderImpl " + message + " : ", value);
    }

    public boolean validateToken(String token, HttpServletRequest request) {
        return this.config.getStrategy() == JwtStrategyType.JWS ? this.validateJwsToken(token, request) : this.validateJweToken(token, request);
    }

    private boolean validateJwsToken(String token, HttpServletRequest request) {
        try {
            Claims claims = (Claims) Jwts.parserBuilder().setSigningKey(this.jwsKey).build().parseClaimsJws(token).getBody();
            this.debugMessage("Claim Information", claims.toString());
            this.debugMessage("Fingerprint", claims.get("fingerprint").toString());
            this.debugMessage("Device information", claims.get("deviceHash").toString());
            return this.performCommonValidation((Map) claims.get("fingerprint"), (String) claims.get("deviceHash"), request);
        } catch (ExpiredJwtException var4) {
            throw new JwtTokenExpiredException("Access Token Expired");
        } catch (JwtTokenExpiredException e) {
            throw e;
        } catch (Exception e) {
            this.loggingService.logError("[JWT 검증 실패] " + e.getMessage());
            return false;
        }
    }

    private boolean validateJweToken(String token, HttpServletRequest request) {
        try {
            JWEObject jweObject = JWEObject.parse(token);
            jweObject.decrypt(new DirectDecrypter(this.jweKey));
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            this.debugMessage("Come in as jwe", claims.toString());
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime != null && (new Date()).after(expirationTime)) {
                throw new JwtTokenExpiredException("Access Token Expired");
            } else {
                return this.performCommonValidation((Map) claims.getClaim("fingerprint"), (String) claims.getClaim("deviceHash"), request);
            }
        } catch (JwtTokenExpiredException e) {
            throw e;
        } catch (Exception e) {
            this.loggingService.logInfo("[JWE Verification failed] " + e.getMessage());
            return false;
        }
    }

    private boolean performCommonValidation(Map<String, Object> tokenFingerprint, String tokenDeviceHash, HttpServletRequest request) {
        if (!this.isProduction()) {
            this.loggingService.logDebug("Device hash and fingerprint verification is omitted in the development environment.");
            return true;
        } else {
            Map<String, Object> currentFingerprint = this.fingerprintGenerator.generate(request);
            boolean fingerprintValid = tokenFingerprint.equals(currentFingerprint);
            Map<String, Object> currentDeviceInfo = this.deviceInfoManager.collectDeviceInfo(request);
            String currentDeviceHash = this.deviceInfoManager.createDeviceHash(currentDeviceInfo);
            boolean deviceHashValid = tokenDeviceHash.equals(currentDeviceHash);
            this.loggingService.logDebug("Fingerprint verification : " + currentFingerprint.toString());
            this.loggingService.logDebug("Fingerprint verification1 : " + fingerprintValid);
            this.loggingService.logDebug("Device Hash Verification : " + String.valueOf(currentDeviceInfo));
            this.loggingService.logDebug("Device Hash Verification 2 : " + currentDeviceHash);
            this.loggingService.logDebug("Device Hash Verification 3 2 It should be like this : " + currentDeviceHash);
            return fingerprintValid && deviceHashValid;
        }
    }

    public UUID getUserId(String token) {
        if (this.config.getStrategy() == JwtStrategyType.JWS) {
            Claims claims = (Claims) Jwts.parserBuilder().setSigningKey(this.jwsKey).build().parseClaimsJws(token).getBody();
            return UUID.fromString(claims.getSubject());
        } else {
            try {
                JWEObject jweObject = JWEObject.parse(token);
                jweObject.decrypt(new DirectDecrypter(this.jweKey));
                SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
                return UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
            } catch (Exception e) {
                throw new RuntimeException("JWE Failed to parse user information", e);
            }
        }
    }

    public String createRefreshToken(UUID userId, Set<String> permissions, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> deviceInfo = this.deviceInfoManager.collectDeviceInfo(request);
        String deviceHash = this.deviceInfoManager.createDeviceHash(deviceInfo);
        Map<String, Object> fingerprint = this.fingerprintGenerator.generate(request);
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = Date.from(Instant.now().plusSeconds(this.config.getRefreshTokenExpireSeconds()));
        String refreshToken;
        if (this.config.getStrategy() == JwtStrategyType.JWS) {
            refreshToken = this.createJwsToken(userId, permissions, deviceInfo, deviceHash, fingerprint, jti, now, expiryDate);
        } else {
            refreshToken = this.createJweToken(userId, permissions, deviceInfo, deviceHash, fingerprint, jti, now, expiryDate);
        }

        this.setRefreshTokenCookie(refreshToken, response);
        return refreshToken;
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken).httpOnly(true).secure(true).sameSite("None").path("/").maxAge(this.config.getRefreshTokenExpireSeconds()).build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "").httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0L).build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public List<String> getAuthoritiesFromToken(String token) {
        Object permissions;
        if (this.config.getStrategy() == JwtStrategyType.JWS) {
            Claims claims = (Claims) Jwts.parserBuilder().setSigningKey(this.jwsKey).build().parseClaimsJws(token).getBody();
            permissions = claims.get("perm");
        } else {
            try {
                JWEObject jweObject = JWEObject.parse(token);
                jweObject.decrypt(new DirectDecrypter(this.jweKey));
                SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
                permissions = signedJWT.getJWTClaimsSet().getClaim("perm");
            } catch (Exception e) {
                throw new RuntimeException("JWE Failed to parse authority information", e);
            }
        }

        return permissions instanceof List ? (List) ((List) permissions).stream().map(Object::toString).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Generated
    public TokenProviderImpl(final MemberProperties config, final FingerprintGenerator fingerprintGenerator, final LoggingService loggingService, final DeviceInfoManager deviceInfoManager) {
        this.config = config;
        this.fingerprintGenerator = fingerprintGenerator;
        this.loggingService = loggingService;
        this.deviceInfoManager = deviceInfoManager;
    }
}
