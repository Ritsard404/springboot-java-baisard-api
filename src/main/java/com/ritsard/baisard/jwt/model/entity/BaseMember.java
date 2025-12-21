package com.ritsard.baisard.jwt.model.entity;


import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.utils.helper.EncryptedFieldConverter;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;

import lombok.Generated;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(
        name = "base_member",
        indexes = {@Index(
                name = "idx_base_member_is_deleted",
                columnList = "is_deleted"
        ), @Index(
                name = "idx_base_member_created_at",
                columnList = "created_at"
        ), @Index(
                name = "idx_base_member_email",
                columnList = "email"
        ), @Index(
                name = "idx_base_member_phone",
                columnList = "phone_number"
        ), @Index(
                name = "idx_base_member_nickname",
                columnList = "nickname"
        )},
        uniqueConstraints = {@UniqueConstraint(
                name = "uk_base_member_nickname_is_deleted",
                columnNames = {"nickname", "is_deleted"}
        ), @UniqueConstraint(
                name = "uk_base_member_phone_is_deleted",
                columnNames = {"phone_number", "is_deleted"}
        )}
)
@SQLDelete(
        sql = "UPDATE base_member SET is_deleted = true, deleted_at = NOW() WHERE uuid_member = ?"
)
@SQLRestriction("is_deleted = false")
@Inheritance(
        strategy = InheritanceType.JOINED
)
public class BaseMember extends BaseEntity {
    @Id
    @Column(
            name = "uuid_member",
            nullable = false
    )
    protected UUID uuidMember;
    @Column(
            name = "name",
            length = 50
    )
    protected String name;
    @Column(
            name = "nickname",
            length = 50
    )
    protected String nickname;
    @Column(
            name = "email",
            length = 100
    )
    protected String email;
    @Convert(
            converter = EncryptedFieldConverter.class
    )
    @Column(
            name = "phone_number",
            length = 256
    )
    protected String phoneNumber;
    @Column(
            name = "birthdate"
    )
    protected LocalDate birthdate;
    @OneToMany(
            mappedBy = "member",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    protected List<LoginCredential> loginCredentials;
    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "member_permission",
            joinColumns = {@JoinColumn(
                    name = "member_uuid"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "permission_id"
            )}
    )
    protected Set<Permission> permissions;
    @Column(
            name = "last_login_at"
    )
    protected Instant lastLoginAt;

    public void deleteMember() {
        super.softDelete();
    }

    public void restoreMember() {
        super.restore();
    }

    @Generated
    private static UUID $default$uuidMember() {
        return UUIDManager.generateUUIDv7();
    }

    @Generated
    private static List<LoginCredential> $default$loginCredentials() {
        return new ArrayList();
    }

    @Generated
    private static Set<Permission> $default$permissions() {
        return new HashSet();
    }

    @Generated
    protected BaseMember(final BaseMemberBuilder<?, ?> b) {
        super(b);
        if (b.uuidMember$set) {
            this.uuidMember = b.uuidMember$value;
        } else {
            this.uuidMember = $default$uuidMember();
        }

        this.name = b.name;
        this.nickname = b.nickname;
        this.email = b.email;
        this.phoneNumber = b.phoneNumber;
        this.birthdate = b.birthdate;
        if (b.loginCredentials$set) {
            this.loginCredentials = b.loginCredentials$value;
        } else {
            this.loginCredentials = $default$loginCredentials();
        }

        if (b.permissions$set) {
            this.permissions = b.permissions$value;
        } else {
            this.permissions = $default$permissions();
        }

        this.lastLoginAt = b.lastLoginAt;
    }

    @Generated
    public static BaseMemberBuilder<?, ?> builder() {
        return new BaseMemberBuilderImpl();
    }

    @Generated
    public BaseMemberBuilder<?, ?> toBuilder() {
        return (new BaseMemberBuilderImpl()).$fillValuesFrom(this);
    }

    @Generated
    public UUID getUuidMember() {
        return this.uuidMember;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getNickname() {
        return this.nickname;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @Generated
    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    @Generated
    public List<LoginCredential> getLoginCredentials() {
        return this.loginCredentials;
    }

    @Generated
    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    @Generated
    public Instant getLastLoginAt() {
        return this.lastLoginAt;
    }

    @Generated
    public void setUuidMember(final UUID uuidMember) {
        this.uuidMember = uuidMember;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
    }

    @Generated
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Generated
    public void setBirthdate(final LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Generated
    public void setLoginCredentials(final List<LoginCredential> loginCredentials) {
        this.loginCredentials = loginCredentials;
    }

    @Generated
    public void setPermissions(final Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Generated
    public void setLastLoginAt(final Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Generated
    protected BaseMember() {
        this.uuidMember = $default$uuidMember();
        this.loginCredentials = $default$loginCredentials();
        this.permissions = $default$permissions();
    }

    @Generated
    public BaseMember(final UUID uuidMember, final String name, final String nickname, final String email, final String phoneNumber, final LocalDate birthdate, final List<LoginCredential> loginCredentials, final Set<Permission> permissions, final Instant lastLoginAt) {
        this.uuidMember = uuidMember;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.loginCredentials = loginCredentials;
        this.permissions = permissions;
        this.lastLoginAt = lastLoginAt;
    }

    @Generated
    public abstract static class BaseMemberBuilder<C extends BaseMember, B extends BaseMemberBuilder<C, B>> extends BaseEntity.BaseEntityBuilder<C, B> {
        @Generated
        private boolean uuidMember$set;
        @Generated
        private UUID uuidMember$value;
        @Generated
        private String name;
        @Generated
        private String nickname;
        @Generated
        private String email;
        @Generated
        private String phoneNumber;
        @Generated
        private LocalDate birthdate;
        @Generated
        private boolean loginCredentials$set;
        @Generated
        private List<LoginCredential> loginCredentials$value;
        @Generated
        private boolean permissions$set;
        @Generated
        private Set<Permission> permissions$value;
        @Generated
        private Instant lastLoginAt;

        @Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return (B)this.self();
        }

        @Generated
        private static void $fillValuesFromInstanceIntoBuilder(final BaseMember instance, final BaseMemberBuilder<?, ?> b) {
            b.uuidMember(instance.uuidMember);
            b.name(instance.name);
            b.nickname(instance.nickname);
            b.email(instance.email);
            b.phoneNumber(instance.phoneNumber);
            b.birthdate(instance.birthdate);
            b.loginCredentials(instance.loginCredentials);
            b.permissions(instance.permissions);
            b.lastLoginAt(instance.lastLoginAt);
        }

        @Generated
        public B uuidMember(final UUID uuidMember) {
            this.uuidMember$value = uuidMember;
            this.uuidMember$set = true;
            return (B)this.self();
        }

        @Generated
        public B name(final String name) {
            this.name = name;
            return (B)this.self();
        }

        @Generated
        public B nickname(final String nickname) {
            this.nickname = nickname;
            return (B)this.self();
        }

        @Generated
        public B email(final String email) {
            this.email = email;
            return (B)this.self();
        }

        @Generated
        public B phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return (B)this.self();
        }

        @Generated
        public B birthdate(final LocalDate birthdate) {
            this.birthdate = birthdate;
            return (B)this.self();
        }

        @Generated
        public B loginCredentials(final List<LoginCredential> loginCredentials) {
            this.loginCredentials$value = loginCredentials;
            this.loginCredentials$set = true;
            return (B)this.self();
        }

        @Generated
        public B permissions(final Set<Permission> permissions) {
            this.permissions$value = permissions;
            this.permissions$set = true;
            return (B)this.self();
        }

        @Generated
        public B lastLoginAt(final Instant lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return (B)this.self();
        }

        @Generated
        protected abstract B self();

        @Generated
        public abstract C build();

        @Generated
        public String toString() {
            String var10000 = super.toString();
            return "BaseMember.BaseMemberBuilder(super=" + var10000 + ", uuidMember$value=" + String.valueOf(this.uuidMember$value) + ", name=" + this.name + ", nickname=" + this.nickname + ", email=" + this.email + ", phoneNumber=" + this.phoneNumber + ", birthdate=" + String.valueOf(this.birthdate) + ", loginCredentials$value=" + String.valueOf(this.loginCredentials$value) + ", permissions$value=" + String.valueOf(this.permissions$value) + ", lastLoginAt=" + String.valueOf(this.lastLoginAt) + ")";
        }
    }

    @Generated
    private static final class BaseMemberBuilderImpl extends BaseMemberBuilder<BaseMember, BaseMemberBuilderImpl> {
        @Generated
        protected BaseMemberBuilderImpl self() {
            return this;
        }

        @Generated
        public BaseMember build() {
            return new BaseMember(this);
        }
    }
}
