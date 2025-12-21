package com.ritsard.baisard.jwt.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.Generated;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
@Table(
        name = "permission",
        indexes = {@Index(
                name = "idx_permission_type",
                columnList = "permission_type",
                unique = true
        )}
)
public class Permission implements GrantedAuthority {
    @Id
    @Column(
            name = "id_permission",
            nullable = false,
            unique = true,
            updatable = false
    )
    protected UUID uuidPermission;
    @Column(
            name = "permission_type",
            nullable = false,
            length = 10
    )
    protected String permissionType;

    @JsonProperty("authority")
    public String getAuthority() {
        return this.permissionType;
    }

    @Generated
    private static UUID $default$uuidPermission() {
        return UUIDManager.generateUUIDv7();
    }

    @Generated
    public static PermissionBuilder builder() {
        return new PermissionBuilder();
    }

    @Generated
    public UUID getUuidPermission() {
        return this.uuidPermission;
    }

    @Generated
    public String getPermissionType() {
        return this.permissionType;
    }

    @Generated
    public void setUuidPermission(final UUID uuidPermission) {
        this.uuidPermission = uuidPermission;
    }

    @Generated
    public void setPermissionType(final String permissionType) {
        this.permissionType = permissionType;
    }

    @Generated
    public Permission() {
        this.uuidPermission = $default$uuidPermission();
    }

    @Generated
    public Permission(final UUID uuidPermission, final String permissionType) {
        this.uuidPermission = uuidPermission;
        this.permissionType = permissionType;
    }

    @Generated
    public static class PermissionBuilder {
        @Generated
        private boolean uuidPermission$set;
        @Generated
        private UUID uuidPermission$value;
        @Generated
        private String permissionType;

        @Generated
        PermissionBuilder() {
        }

        @Generated
        public PermissionBuilder uuidPermission(final UUID uuidPermission) {
            this.uuidPermission$value = uuidPermission;
            this.uuidPermission$set = true;
            return this;
        }

        @Generated
        public PermissionBuilder permissionType(final String permissionType) {
            this.permissionType = permissionType;
            return this;
        }

        @Generated
        public Permission build() {
            UUID uuidPermission$value = this.uuidPermission$value;
            if (!this.uuidPermission$set) {
                uuidPermission$value = $default$uuidPermission();
            }

            return new Permission(uuidPermission$value, this.permissionType);
        }

        @Generated
        public String toString() {
            String var10000 = String.valueOf(this.uuidPermission$value);
            return "Permission.PermissionBuilder(uuidPermission$value=" + var10000 + ", permissionType=" + this.permissionType + ")";
        }
    }
}
