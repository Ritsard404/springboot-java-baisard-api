package com.ritsard.baisard.domain.member.enums;

import com.ritsard.baisard.jwt.utils.permission.IPermissionType;
import com.ritsard.baisard.jwt.utils.permission.PermissionTypeProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PermissionType implements PermissionTypeProvider {
    SUPERADMIN("Super Admin"),
    ADMIN("Admin"),
    CASHIER("Cashier"),
    USER("User");

    private final String description;

    @Override
    public IPermissionType[] getPermissionTypes() {
        return Arrays.stream(PermissionType.values()).toArray(IPermissionType[]::new);
    }

}
