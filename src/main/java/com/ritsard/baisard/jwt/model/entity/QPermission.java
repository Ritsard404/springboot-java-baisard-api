package com.ritsard.baisard.jwt.model.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import java.util.UUID;

public class QPermission extends EntityPathBase<Permission> {
    private static final long serialVersionUID = -1308138362L;
    public static final QPermission permission = new QPermission("permission");
    public final StringPath permissionType = this.createString("permissionType");
    public final ComparablePath<UUID> uuidPermission = this.createComparable("uuidPermission", UUID.class);

    public QPermission(String variable) {
        super(Permission.class, PathMetadataFactory.forVariable(variable));
    }

    public QPermission(Path<? extends Permission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPermission(PathMetadata metadata) {
        super(Permission.class, metadata);
    }
}
