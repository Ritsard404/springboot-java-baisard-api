package com.ritsard.baisard.jwt.model.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import java.util.UUID;

public class QPermissionManual extends EntityPathBase<Permission> {
    private static final long serialVersionUID = -1308138362L;
    public static final QPermissionManual permission = new QPermissionManual("permission");
    public final StringPath permissionType = this.createString("permissionType");
    public final ComparablePath<UUID> uuidPermission = this.createComparable("uuidPermission", UUID.class);

    public QPermissionManual(String variable) {
        super(Permission.class, PathMetadataFactory.forVariable(variable));
    }

    public QPermissionManual(Path<? extends Permission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPermissionManual(PathMetadata metadata) {
        super(Permission.class, metadata);
    }
}
