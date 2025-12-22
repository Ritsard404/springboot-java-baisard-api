package com.ritsard.baisard.jwt.model.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.*;
import com.ritsard.baisard.base.entity.QBaseEntity;
import com.ritsard.baisard.jwt.model.enums.LoginType;

import java.time.Instant;
import java.util.UUID;

public class QLoginCredential extends EntityPathBase<LoginCredential> {
    private static final long serialVersionUID = -1469956023L;
    private static final PathInits INITS;
    public static final QLoginCredential loginCredential;
    public final QBaseEntity _super;
    public final DateTimePath<Instant> createdAt;
    public final DateTimePath<Instant> deletedAt;
    public final StringPath identifier;
    public final BooleanPath isDeleted;
    public final EnumPath<LoginType> loginType;
    public final QBaseMember member;
    public final StringPath password;
    public final DateTimePath<Instant> updatedAt;
    public final ComparablePath<UUID> uuidLoginCredential;

    public QLoginCredential(String variable) {
        this(LoginCredential.class, PathMetadataFactory.forVariable(variable), INITS);
    }

    public QLoginCredential(Path<? extends LoginCredential> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLoginCredential(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLoginCredential(PathMetadata metadata, PathInits inits) {
        this(LoginCredential.class, metadata, inits);
    }

    public QLoginCredential(Class<? extends LoginCredential> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseEntity(this);
        this.createdAt = this._super.createdAt;
        this.deletedAt = this._super.deletedAt;
        this.identifier = this.createString("identifier");
        this.isDeleted = this._super.isDeleted;
        this.loginType = this.createEnum("loginType", LoginType.class);
        this.password = this.createString("password");
        this.updatedAt = this._super.updatedAt;
        this.uuidLoginCredential = this.createComparable("uuidLoginCredential", UUID.class);
        this.member = inits.isInitialized("member") ? new QBaseMember(this.forProperty("member")) : null;
    }

    static {
        INITS = PathInits.DIRECT2;
        loginCredential = new QLoginCredential("loginCredential");
    }
}
