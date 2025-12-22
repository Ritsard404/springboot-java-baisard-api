package com.ritsard.baisard.jwt.utils;

import com.ritsard.baisard.jwt.model.entity.BaseMember;

import java.util.UUID;

public interface AuthManager<T extends BaseMember> {
    UUID getBaseMemberUuid();

    T getMember();

    T getMemberWithAllDetails();

    T getMemberWithPermissions();

    T getMemberWithCredentials();

    void updateLastLoginTime();

    boolean isAuthenticated();

    boolean isCurrentUser(UUID memberId);

    boolean hasPermission(String permissionName);

    boolean hasRole(String roleName);

    boolean isAdmin();

    boolean isAccountActive();
}