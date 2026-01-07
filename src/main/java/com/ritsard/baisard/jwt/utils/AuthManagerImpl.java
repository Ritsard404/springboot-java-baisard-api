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
@Transactional(readOnly = true)
public class AuthManagerImpl<T extends BaseMember> implements AuthManager<T> {

    @Generated
    private static final Logger log = LoggerFactory.getLogger(AuthManagerImpl.class);

    private final BaseMemberRepository<T> memberRepository;
    private final LoggingService loggingService;

    public UUID getBaseMemberUuid() {
        UUID uuid = this.getUuidFromToken();
        this.loggingService.logInfo("Fetching authenticated user UUID: " + uuid);

        return this.memberRepository.findById(uuid)
                .filter(member -> !member.isDeleted())
                .map(BaseMember::getUuidMember)
                .orElseThrow(() -> {
                    log.warn("Invalid user or deleted account: {}", uuid);
                    return new InvalidTokenRequestException();
                });
    }

    public T getMember() {
        UUID uuid = this.getUuidFromToken();
        log.debug("Fetching user (basic information): {}", uuid);

        return this.memberRepository.findWithCredentialsByUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", uuid);
                    return new InvalidTokenRequestException();
                });
    }

    public T getMemberWithAllDetails() {
        UUID uuid = this.getUuidFromToken();
        log.debug("Fetching user (all details): {}", uuid);

        return this.memberRepository.findWithAllDetailsByUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("User not found (all details): {}", uuid);
                    return new InvalidTokenRequestException();
                });
    }

    public T getMemberWithPermissions() {
        UUID uuid = this.getUuidFromToken();
        log.debug("Fetching user (permissions): {}", uuid);

        return this.memberRepository.findWithPermissionsByUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("User not found (permissions): {}", uuid);
                    return new InvalidTokenRequestException();
                });
    }

    public T getMemberWithCredentials() {
        UUID uuid = this.getUuidFromToken();
        log.debug("Fetching user (credentials): {}", uuid);

        return this.memberRepository.findWithCredentialsByUuid(uuid)
                .orElseThrow(() -> {
                    log.warn("User not found (credentials): {}", uuid);
                    return new InvalidTokenRequestException();
                });
    }

    @Transactional
    public void updateLastLoginTime() {
        T member = this.getMember();
        Instant now = Instant.now();

        member.setLastLoginAt(now);
        this.memberRepository.save(member);

        log.info(
                "Last login time updated: userId={}, loginTime={}",
                member.getUuidMember(),
                now
        );
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated =
                authentication != null &&
                        authentication.isAuthenticated() &&
                        !"anonymousUser".equals(authentication.getName());

        log.debug("Authentication status check: {}", authenticated);
        return authenticated;
    }

    public boolean isCurrentUser(UUID memberId) {
        if (!this.isAuthenticated()) {
            log.debug("Unauthenticated user");
            return false;
        }

        try {
            UUID currentUserId = this.getUuidFromToken();
            boolean isMatch = currentUserId.equals(memberId);

            log.debug(
                    "User match check: currentUser={}, targetUser={}, match={}",
                    currentUserId,
                    memberId,
                    isMatch
            );
            return isMatch;
        } catch (InvalidTokenRequestException e) {
            log.debug("Failed to extract user ID from token");
            return false;
        }
    }

    public boolean hasPermission(String permissionName) {
        try {
            T member = this.getMemberWithPermissions();

            boolean hasPermission = member.getPermissions()
                    .stream()
                    .anyMatch(permission ->
                            permission.getPermissionType().equals(permissionName)
                    );

            log.debug(
                    "Permission check: userId={}, permission={}, hasPermission={}",
                    member.getUuidMember(),
                    permissionName,
                    hasPermission
            );
            return hasPermission;
        } catch (InvalidTokenRequestException e) {
            log.debug("Permission check failed - invalid token");
            return false;
        }
    }

    public boolean hasRole(String roleName) {
        try {
            T member = this.getMemberWithPermissions();

            boolean hasRole = member.getPermissions()
                    .stream()
                    .anyMatch(role ->
                            role.getPermissionType().equals(roleName)
                    );

            log.debug(
                    "Role check: userId={}, role={}, hasRole={}",
                    member.getUuidMember(),
                    roleName,
                    hasRole
            );
            return hasRole;
        } catch (InvalidTokenRequestException e) {
            log.debug("Role check failed - invalid token");
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

            log.debug(
                    "Account status check: userId={}, isActive={}",
                    member.getUuidMember(),
                    isActive
            );
            return isActive;
        } catch (InvalidTokenRequestException e) {
            log.debug("Account status check failed - invalid token");
            return false;
        }
    }

    private UUID getUuidFromToken() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String name = authentication.getName();

            try {
                UUID uuid = UUID.fromString(name);
                log.debug("Successfully extracted UUID from token: {}", uuid);
                return uuid;
            } catch (IllegalArgumentException e) {
                log.warn(
                        "Failed to extract UUID from token - invalid format: {}",
                        name
                );
                this.loggingService.logInfo(
                        "[Warning] authentication.getName() is not a UUID: " + name
                );
                throw new InvalidTokenRequestException();
            }
        }

        log.debug("No authentication information or not authenticated");
        throw new InvalidTokenRequestException();
    }
}
