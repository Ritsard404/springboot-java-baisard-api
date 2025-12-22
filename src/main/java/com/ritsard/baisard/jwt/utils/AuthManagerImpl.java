package com.ritsard.baisard.jwt.utils;

import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.utils.exceptions.InvalidTokenRequestException;
import com.ritsard.baisard.utils.log.LoggingService;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(
        readOnly = true
)
public class AuthManagerImpl<T extends BaseMember> implements AuthManager<T> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AuthManagerImpl.class);
    private final BaseMemberRepository<T> memberRepository;
    private final LoggingService loggingService;

    public UUID getBaseMemberUuid() {
        UUID uuid = this.getUuidFromToken();
        this.loggingService.logInfo("인증된 사용자 UUID 조회: " + String.valueOf(uuid));
        return (UUID)this.memberRepository.findById(uuid).filter((member) -> !member.isDeleted()).map(BaseMember::getUuidMember).orElseThrow(() -> {
            log.warn("유효하지 않은 사용자 또는 삭제된 계정: {}", uuid);
            return new InvalidTokenRequestException();
        });
    }

    public T getMember() {
        UUID uuid = this.getUuidFromToken();
        log.debug("사용자 조회 (기본 정보): {}", uuid);
        return (T)(this.memberRepository.findWithCredentialsByUuid(uuid).orElseThrow(() -> {
            log.warn("사용자를 찾을 수 없음: {}", uuid);
            return new InvalidTokenRequestException();
        }));
    }

    public T getMemberWithAllDetails() {
        UUID uuid = this.getUuidFromToken();
        log.debug("사용자 조회 (모든 정보): {}", uuid);
        return (T)(this.memberRepository.findWithAllDetailsByUuid(uuid).orElseThrow(() -> {
            log.warn("사용자를 찾을 수 없음 (전체 정보): {}", uuid);
            return new InvalidTokenRequestException();
        }));
    }

    public T getMemberWithPermissions() {
        UUID uuid = this.getUuidFromToken();
        log.debug("사용자 조회 (권한 정보): {}", uuid);
        return (T)(this.memberRepository.findWithPermissionsByUuid(uuid).orElseThrow(() -> {
            log.warn("사용자를 찾을 수 없음 (권한 정보): {}", uuid);
            return new InvalidTokenRequestException();
        }));
    }

    public T getMemberWithCredentials() {
        UUID uuid = this.getUuidFromToken();
        log.debug("사용자 조회 (자격정보): {}", uuid);
        return (T)(this.memberRepository.findWithCredentialsByUuid(uuid).orElseThrow(() -> {
            log.warn("사용자를 찾을 수 없음 (자격정보): {}", uuid);
            return new InvalidTokenRequestException();
        }));
    }

    @Transactional
    public void updateLastLoginTime() {
        T member = this.getMember();
        Instant now = Instant.now();
        member.setLastLoginAt(now);
        this.memberRepository.save(member);
        log.info("마지막 로그인 시간 업데이트: userId={}, loginTime={}", member.getUuidMember(), now);
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        log.debug("인증 상태 확인: {}", authenticated);
        return authenticated;
    }

    public boolean isCurrentUser(UUID memberId) {
        if (!this.isAuthenticated()) {
            log.debug("인증되지 않은 사용자");
            return false;
        } else {
            try {
                UUID currentUserId = this.getUuidFromToken();
                boolean isMatch = currentUserId.equals(memberId);
                log.debug("사용자 일치 확인: currentUser={}, targetUser={}, match={}", new Object[]{currentUserId, memberId, isMatch});
                return isMatch;
            } catch (InvalidTokenRequestException var4) {
                log.debug("토큰에서 사용자 ID 추출 실패");
                return false;
            }
        }
    }

    public boolean hasPermission(String permissionName) {
        try {
            T member = this.getMemberWithPermissions();
            boolean hasPermission = member.getPermissions().stream().anyMatch((permission) -> permission.getPermissionType().equals(permissionName));
            log.debug("권한 확인: userId={}, permission={}, hasPermission={}", new Object[]{member.getUuidMember(), permissionName, hasPermission});
            return hasPermission;
        } catch (InvalidTokenRequestException var4) {
            log.debug("권한 확인 실패 - 유효하지 않은 토큰");
            return false;
        }
    }

    public boolean hasRole(String roleName) {
        try {
            T member = this.getMemberWithPermissions();
            boolean hasRole = member.getPermissions().stream().anyMatch((role) -> role.getPermissionType().equals(roleName));
            log.debug("역할 확인: userId={}, role={}, hasRole={}", new Object[]{member.getUuidMember(), roleName, hasRole});
            return hasRole;
        } catch (InvalidTokenRequestException var4) {
            log.debug("역할 확인 실패 - 유효하지 않은 토큰");
            return false;
        }
    }

    public boolean isAdmin() {
        return this.hasRole("ADMIN") || this.hasPermission("ADMIN_ACCESS");
    }

    public boolean isAccountActive() {
        try {
            T member = this.getMember();
            boolean isActive = member.isActive() && !member.isDeleted();
            log.debug("계정 상태 확인: userId={}, isActive={}", member.getUuidMember(), isActive);
            return isActive;
        } catch (InvalidTokenRequestException var3) {
            log.debug("계정 상태 확인 실패 - 유효하지 않은 토큰");
            return false;
        }
    }

    private UUID getUuidFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String name = authentication.getName();

            try {
                UUID uuid = UUID.fromString(name);
                log.debug("토큰에서 UUID 추출 성공: {}", uuid);
                return uuid;
            } catch (IllegalArgumentException var4) {
                log.warn("토큰에서 UUID 추출 실패 - 유효하지 않은 형식: {}", name);
                this.loggingService.logInfo("[경고] authentication.getName()이 UUID 아님: " + name);
                throw new InvalidTokenRequestException();
            }
        } else {
            log.debug("인증 정보가 없거나 인증되지 않음");
            throw new InvalidTokenRequestException();
        }
    }
}
