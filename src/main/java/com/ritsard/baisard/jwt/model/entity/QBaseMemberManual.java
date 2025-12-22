package com.ritsard.baisard.jwt.model.entity;


import com.ritsard.baisard.base.entity.QBaseEntityManual;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.StringPath;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class QBaseMemberManual extends EntityPathBase<BaseMember> {
    private static final long serialVersionUID = -1794049150L;
    public static final QBaseMemberManual baseMember = new QBaseMemberManual("baseMember");
    public final QBaseEntityManual _super = new QBaseEntityManual(this);
    public final DatePath<LocalDate> birthdate = this.createDate("birthdate", LocalDate.class);
    public final DateTimePath<Instant> createdAt;
    public final DateTimePath<Instant> deletedAt;
    public final StringPath email;
    public final BooleanPath isDeleted;
    public final DateTimePath<Instant> lastLoginAt;
    public final ListPath<LoginCredential, QLoginCredentialManual> loginCredentials;
    public final StringPath name;
    public final StringPath nickname;
    public final SetPath<Permission, QPermissionManual> permissions;
    public final StringPath phoneNumber;
    public final DateTimePath<Instant> updatedAt;
    public final ComparablePath<UUID> uuidMember;

    public QBaseMemberManual(String variable) {
        super(BaseMember.class, PathMetadataFactory.forVariable(variable));
        this.createdAt = this._super.createdAt;
        this.deletedAt = this._super.deletedAt;
        this.email = this.createString("email");
        this.isDeleted = this._super.isDeleted;
        this.lastLoginAt = this.createDateTime("lastLoginAt", Instant.class);
        this.loginCredentials = this.createList("loginCredentials", LoginCredential.class, QLoginCredentialManual.class, PathInits.DIRECT2);
        this.name = this.createString("name");
        this.nickname = this.createString("nickname");
        this.permissions = this.createSet("permissions", Permission.class, QPermissionManual.class, PathInits.DIRECT2);
        this.phoneNumber = this.createString("phoneNumber");
        this.updatedAt = this._super.updatedAt;
        this.uuidMember = this.createComparable("uuidMember", UUID.class);
    }

    public QBaseMemberManual(Path<? extends BaseMember> path) {
        super(path.getType(), path.getMetadata());
        this.createdAt = this._super.createdAt;
        this.deletedAt = this._super.deletedAt;
        this.email = this.createString("email");
        this.isDeleted = this._super.isDeleted;
        this.lastLoginAt = this.createDateTime("lastLoginAt", Instant.class);
        this.loginCredentials = this.createList("loginCredentials", LoginCredential.class, QLoginCredentialManual.class, PathInits.DIRECT2);
        this.name = this.createString("name");
        this.nickname = this.createString("nickname");
        this.permissions = this.createSet("permissions", Permission.class, QPermissionManual.class, PathInits.DIRECT2);
        this.phoneNumber = this.createString("phoneNumber");
        this.updatedAt = this._super.updatedAt;
        this.uuidMember = this.createComparable("uuidMember", UUID.class);
    }

    public QBaseMemberManual(PathMetadata metadata) {
        super(BaseMember.class, metadata);
        this.createdAt = this._super.createdAt;
        this.deletedAt = this._super.deletedAt;
        this.email = this.createString("email");
        this.isDeleted = this._super.isDeleted;
        this.lastLoginAt = this.createDateTime("lastLoginAt", Instant.class);
        this.loginCredentials = this.createList("loginCredentials", LoginCredential.class, QLoginCredentialManual.class, PathInits.DIRECT2);
        this.name = this.createString("name");
        this.nickname = this.createString("nickname");
        this.permissions = this.createSet("permissions", Permission.class, QPermissionManual.class, PathInits.DIRECT2);
        this.phoneNumber = this.createString("phoneNumber");
        this.updatedAt = this._super.updatedAt;
        this.uuidMember = this.createComparable("uuidMember", UUID.class);
    }
}
