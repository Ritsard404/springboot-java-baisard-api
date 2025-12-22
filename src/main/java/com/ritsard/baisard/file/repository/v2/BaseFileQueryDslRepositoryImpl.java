package com.ritsard.baisard.file.repository.v2;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.base.repository.BaseQueryDslRepositoryImpl;
import com.ritsard.baisard.file.dto.v2.FileStatisticsDto;
import com.ritsard.baisard.file.entity.BaseFile;
import com.ritsard.baisard.file.entity.QBaseFile;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.util.StringUtils;

public class BaseFileQueryDslRepositoryImpl<T extends BaseFile, S extends FileStatisticsDto> extends BaseQueryDslRepositoryImpl<T, UUID> implements BaseFileQueryDslRepository<T, S> {
    protected final QBaseFile file;

    public BaseFileQueryDslRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
        this.file = QBaseFile.baseFile;
    }

    protected EntityPathBase<T> getEntityPath() {
        return (EntityPathBase<T>) this.file;
    }

    protected SimpleExpression<UUID> getIdPath() {
        return this.file.uuidBaseFile;
    }

    protected Class<T> getEntityClass() {
        return (Class<T>) BaseFile.class;
    }

    protected void addDynamicConditions(BooleanBuilder builder, Object searchCondition) {
        if (searchCondition instanceof BaseFileSearchCondition condition) {
            if (StringUtils.hasText(condition.getKeyword())) {
                builder.and(this.file.filename.containsIgnoreCase(condition.getKeyword()).or(this.file.originalFilename.containsIgnoreCase(condition.getKeyword())));
            }

            if (StringUtils.hasText(condition.getMimeType())) {
                builder.and(this.file.mimeType.eq(condition.getMimeType()));
            }

            if (StringUtils.hasText(condition.getFileExtension())) {
                builder.and(this.file.fileExtension.eq(condition.getFileExtension()));
            }

            if (condition.getMinFileSize() != null) {
                builder.and(this.file.fileSize.goe(condition.getMinFileSize()));
            }

            if (condition.getMaxFileSize() != null) {
                builder.and(this.file.fileSize.loe(condition.getMaxFileSize()));
            }

            if (condition.getIsAttached() != null) {
                builder.and(this.file.isAttached.eq(condition.getIsAttached()));
            }

            if (StringUtils.hasText(condition.getEntityType())) {
                builder.and(this.file.entityType.eq(condition.getEntityType()));
            }

            if (StringUtils.hasText(condition.getKeyword())) {
                builder.and(this.file.filename.containsIgnoreCase(condition.getKeyword()).or(this.file.originalFilename.containsIgnoreCase(condition.getKeyword())));
            }

            if (condition.getStartDate() != null && condition.getEndDate() != null) {
                builder.and(this.file.createdAt.between(condition.getStartDate(), condition.getEndDate()));
            }

            if (Boolean.FALSE.equals(condition.getIncludeDeleted())) {
                builder.and(this.file.isDeleted.eq(false));
            }
        }

    }

    public List<T> findUnattachedFilesOlderThan(Instant cutoffTime, int limit) {
        return ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.file).where(this.file.isAttached.eq(false).and(this.file.createdAt.lt(cutoffTime)).and(this.file.isDeleted.eq(false)))).orderBy(this.file.createdAt.asc())).limit((long) limit)).fetch().stream().map((f) -> f).toList();
    }

    public List<T> findDeletedFilesOlderThan(Instant cutoffTime, int limit) {
        return ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.file).where(this.file.isDeleted.eq(true).and(this.file.deletedAt.lt(cutoffTime)))).orderBy(this.file.deletedAt.asc())).limit((long) limit)).fetch().stream().map((f) -> f).toList();
    }

    public List<T> findByEntity(String entityType, UUID entityId) {
        return ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.file).where(this.file.entityType.eq(entityType).and(this.file.entityId.eq(entityId)).and(this.file.isDeleted.eq(false)))).orderBy(this.file.createdAt.desc())).fetch().stream().map((f) -> f).toList();
    }

    public List<T> findAllByIds(List<UUID> ids) {
        return ((JPAQuery) this.queryFactory.selectFrom(this.file).where(this.file.uuidBaseFile.in(ids).and(this.file.isDeleted.eq(false)))).fetch().stream().map((f) -> f).toList();
    }

    public long attachFilesToEntity(List<UUID> fileIds, String entityType, UUID entityId) {
        return this.queryFactory.update(this.file).set(this.file.isAttached, true).set(this.file.entityType, entityType).set(this.file.entityId, entityId).set(this.file.attachedAt, Instant.now()).where(new Predicate[]{this.file.uuidBaseFile.in(fileIds).and(this.file.isDeleted.eq(false))}).execute();
    }

    public long detachFilesFromEntity(String entityType, UUID entityId) {
        return this.queryFactory.update(this.file).set(this.file.isAttached, false).setNull(this.file.entityType).setNull(this.file.entityId).setNull(this.file.attachedAt).where(new Predicate[]{this.file.entityType.eq(entityType).and(this.file.entityId.eq(entityId))}).execute();
    }

    public List<S> getFileStatisticsByType() {
        return ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.select(Projections.constructor(FileStatisticsDto.class, new Expression[]{this.file.mimeType, this.file.count(), this.file.fileSize.sum(), this.file.fileSize.avg()})).from(this.file)).where(this.file.isDeleted.eq(false))).groupBy(this.file.mimeType)).orderBy(this.file.count().desc())).fetch().stream().map((dto) -> dto).toList();
    }

    public List<S> getFileStatisticsByEntity() {
        return ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.select(Projections.constructor(FileStatisticsDto.class, new Expression[]{this.file.entityType, this.file.count(), this.file.fileSize.sum(), this.file.fileSize.avg()})).from(this.file)).where(this.file.isAttached.eq(true).and(this.file.isDeleted.eq(false)))).groupBy(this.file.entityType)).orderBy(this.file.count().desc())).fetch().stream().map((dto) -> dto).toList();
    }

    public List<S> getUploadStatistics(Instant startDate, Instant endDate) {
        return ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.select(Projections.constructor(FileStatisticsDto.class, new Expression[]{this.file.createdAt.yearMonth().stringValue(), this.file.count(), this.file.fileSize.sum(), this.file.fileSize.avg()})).from(this.file)).where(this.file.isDeleted.eq(false).and(this.file.createdAt.between(startDate, endDate)))).groupBy(this.file.createdAt.yearMonth())).orderBy(this.file.createdAt.yearMonth().asc())).fetch().stream().map((dto) -> dto).toList();
    }

    public Long getTotalStorageSize() {
        return (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.file.fileSize.sum()).from(this.file)).where(this.file.isDeleted.eq(false))).fetchOne();
    }
}
