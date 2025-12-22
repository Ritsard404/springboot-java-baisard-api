package com.ritsard.baisard.base.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;

import java.time.Instant;

public class QBaseEntityManual extends EntityPathBase<BaseEntity> {
    private static final long serialVersionUID = -1877579512L;
    public static final QBaseEntityManual baseEntity = new QBaseEntityManual("baseEntity");
    public final DateTimePath<Instant> createdAt = this.createDateTime("createdAt", Instant.class);
    public final DateTimePath<Instant> deletedAt = this.createDateTime("deletedAt", Instant.class);
    public final BooleanPath isDeleted = this.createBoolean("isDeleted");
    public final DateTimePath<Instant> updatedAt = this.createDateTime("updatedAt", Instant.class);

    public QBaseEntityManual(String variable) {
        super(BaseEntity.class, PathMetadataFactory.forVariable(variable));
    }

    public QBaseEntityManual(Path<? extends BaseEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseEntityManual(PathMetadata metadata) {
        super(BaseEntity.class, metadata);
    }
}
