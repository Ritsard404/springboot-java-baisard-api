package com.ritsard.baisard.jwt.model.entity;


import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.jwt.model.enums.LoginType;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.Generated;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(
        name = "login_credential",
        uniqueConstraints = {@UniqueConstraint(
                columnNames = {"login_type", "identifier"}
        )},
        indexes = {@Index(
                name = "idx_login_credential_member",
                columnList = "member_uuid"
        ), @Index(
                name = "idx_login_credential_identifier",
                columnList = "identifier"
        ), @Index(
                name = "idx_login_credential_created",
                columnList = "created_at"
        )}
)
@SQLDelete(
        sql = "UPDATE login_credential SET is_deleted = true, deleted_at = NOW() WHERE id = ?"
)
@SQLRestriction(
        "is_deleted = false"
)
public class LoginCredential extends BaseEntity {
    @Id
    @Column(
            name = "uuid_login_credential",
            nullable = false
    )
    protected UUID uuidLoginCredential;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "member_uuid",
            nullable = false
    )
    private BaseMember member;
    @Enumerated(EnumType.STRING)
    @Column(
            name = "login_type",
            nullable = false,
            length = 20
    )
    private LoginType loginType;
    @Column(
            name = "identifier",
            nullable = false,
            length = 100
    )
    private String identifier;
    @Column(
            name = "password",
            length = 100
    )
    private String password;

    @Generated
    private static UUID $default$uuidLoginCredential() {
        return UUIDManager.generateUUIDv7();
    }

    @Generated
    protected LoginCredential(final LoginCredential.LoginCredentialBuilder<?, ?> b) {
        super(b);
        if (b.uuidLoginCredential$set) {
            this.uuidLoginCredential = b.uuidLoginCredential$value;
        } else {
            this.uuidLoginCredential = $default$uuidLoginCredential();
        }

        this.member = b.member;
        this.loginType = b.loginType;
        this.identifier = b.identifier;
        this.password = b.password;
    }

    @Generated
    public static LoginCredential.LoginCredentialBuilder<?, ?> builder() {
        return new LoginCredential.LoginCredentialBuilderImpl();
    }

    @Generated
    public LoginCredential.LoginCredentialBuilder<?, ?> toBuilder() {
        return (new LoginCredential.LoginCredentialBuilderImpl()).$fillValuesFrom(this);
    }

    @Generated
    public UUID getUuidLoginCredential() {
        return this.uuidLoginCredential;
    }

    @Generated
    public BaseMember getMember() {
        return this.member;
    }

    @Generated
    public LoginType getLoginType() {
        return this.loginType;
    }

    @Generated
    public String getIdentifier() {
        return this.identifier;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public void setUuidLoginCredential(final UUID uuidLoginCredential) {
        this.uuidLoginCredential = uuidLoginCredential;
    }

    @Generated
    public void setMember(final BaseMember member) {
        this.member = member;
    }

    @Generated
    public void setLoginType(final LoginType loginType) {
        this.loginType = loginType;
    }

    @Generated
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    protected LoginCredential() {
        this.uuidLoginCredential = $default$uuidLoginCredential();
    }

    @Generated
    public LoginCredential(final UUID uuidLoginCredential, final BaseMember member, final LoginType loginType, final String identifier, final String password) {
        this.uuidLoginCredential = uuidLoginCredential;
        this.member = member;
        this.loginType = loginType;
        this.identifier = identifier;
        this.password = password;
    }

    @Generated
    public abstract static class LoginCredentialBuilder<C extends LoginCredential, B extends LoginCredential.LoginCredentialBuilder<C, B>> extends BaseEntity.BaseEntityBuilder<C, B> {
        @Generated
        private boolean uuidLoginCredential$set;
        @Generated
        private UUID uuidLoginCredential$value;
        @Generated
        private BaseMember member;
        @Generated
        private LoginType loginType;
        @Generated
        private String identifier;
        @Generated
        private String password;

        @Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return (B)this.self();
        }

        @Generated
        private static void $fillValuesFromInstanceIntoBuilder(final LoginCredential instance, final LoginCredential.LoginCredentialBuilder<?, ?> b) {
            b.uuidLoginCredential(instance.uuidLoginCredential);
            b.member(instance.member);
            b.loginType(instance.loginType);
            b.identifier(instance.identifier);
            b.password(instance.password);
        }

        @Generated
        public B uuidLoginCredential(final UUID uuidLoginCredential) {
            this.uuidLoginCredential$value = uuidLoginCredential;
            this.uuidLoginCredential$set = true;
            return (B)this.self();
        }

        @Generated
        public B member(final BaseMember member) {
            this.member = member;
            return (B)this.self();
        }

        @Generated
        public B loginType(final LoginType loginType) {
            this.loginType = loginType;
            return (B)this.self();
        }

        @Generated
        public B identifier(final String identifier) {
            this.identifier = identifier;
            return (B)this.self();
        }

        @Generated
        public B password(final String password) {
            this.password = password;
            return (B)this.self();
        }

        @Generated
        protected abstract B self();

        @Generated
        public abstract C build();

        @Generated
        public String toString() {
            String var10000 = super.toString();
            return "LoginCredential.LoginCredentialBuilder(super=" + var10000 + ", uuidLoginCredential$value=" + String.valueOf(this.uuidLoginCredential$value) + ", member=" + String.valueOf(this.member) + ", loginType=" + String.valueOf(this.loginType) + ", identifier=" + this.identifier + ", password=" + this.password + ")";
        }
    }

    @Generated
    private static final class LoginCredentialBuilderImpl extends LoginCredential.LoginCredentialBuilder<LoginCredential, LoginCredential.LoginCredentialBuilderImpl> {
        @Generated
        protected LoginCredential.LoginCredentialBuilderImpl self() {
            return this;
        }

        @Generated
        public LoginCredential build() {
            return new LoginCredential(this);
        }
    }
}
