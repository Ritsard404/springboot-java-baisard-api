package com.ritsard.baisard.global.utils;

import com.ritsard.baisard.domain.enums.PermissionType;
import com.ritsard.baisard.domain.member.Member;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationHelper {

    private final AuthManager<BaseMember> authManager;

    public boolean isSelfOrAdmin(UUID uuidMember) {
        return isLoggedIn() && (authManager.isAdmin() || authManager.isCurrentUser(uuidMember));
    }

    public boolean isLoggedIn() {
        return authManager.isAuthenticated();
    }

    public boolean hasRole(PermissionType permissionType) {
        return isLoggedIn() && authManager.hasRole(permissionType.name());
    }

    public boolean isAdmin() {
        return isLoggedIn() && authManager.isAdmin();
    }

    public void assertLoggedIn() {
        log.debug("THE STATUS!: {}", SecurityContextHolder.getContext().getAuthentication());
        if (!authManager.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
    }

    public void assertIsSelfOrAdmin(Member member) {
        if (member == null || !isSelfOrAdmin(member.getUuidMember())) {
            throw new UnauthorizedException("필요한 권한이 없습니다.");
        }
    }

    public void assertIsSelfOrAdmin(UUID uuidMember) {
        if (!isSelfOrAdmin(uuidMember)) {
            throw new UnauthorizedException("필요한 권한이 없습니다.");
        }
    }

    public void assertIsAdmin() {
        if (!isAdmin()) {
            throw new UnauthorizedException("관리자 권한이 필요합니다.");
        }
    }

    public void assertIsSelfOrHasRole(UUID uuidMember, PermissionType permission) {
        if (!isSelfOrAdmin(uuidMember) && !hasRole(permission)) {
            throw new UnauthorizedException("이 작업을 수행할 권한이 없습니다.");
        }
    }
}
