package com.ritsard.baisard.base.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.base.entity.BaseEntity;
import jakarta.persistence.EntityManager;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Transactional(
        readOnly = true
)
public abstract class BaseQueryDslRepositoryImpl<T extends BaseEntity, ID> implements BaseQueryDslRepository<T, ID> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(BaseQueryDslRepositoryImpl.class);
    protected final JPAQueryFactory queryFactory;
    protected final EntityManager entityManager;

    protected abstract EntityPathBase<T> getEntityPath();

    protected abstract SimpleExpression<ID> getIdPath();

    protected abstract Class<T> getEntityClass();

    public Optional<T> findActiveById(ID id) {
        T result = (T) (((JPAQuery) this.queryFactory.selectFrom(this.getEntityPath()).where(this.isNotDeleted().and(this.idEq(id)))).fetchOne());
        return Optional.ofNullable(result);
    }

    public List<T> findActiveByIds(List<ID> ids) {
        return ids != null && !ids.isEmpty() ? ((JPAQuery) this.queryFactory.selectFrom(this.getEntityPath()).where(this.isNotDeleted().and(this.getIdPath().in(ids)))).fetch() : List.of();
    }

    public Page<T> findByConditions(Object searchCondition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(this.isNotDeleted());
        this.addDynamicConditions(builder, searchCondition);
        long total = (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.getEntityPath().count()).from(this.getEntityPath())).where(builder)).fetchOne();
        List<T> content = ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.getEntityPath()).where(builder)).orderBy(this.getOrderSpecifiers(pageable))).offset(pageable.getOffset())).limit((long) pageable.getPageSize())).fetch();
        return new PageImpl(content, pageable, total);
    }

    protected abstract void addDynamicConditions(BooleanBuilder builder, Object searchCondition);

    protected OrderSpecifier<?>[] getDefaultOrderSpecifiers() {
        return new OrderSpecifier[]{this.getCreatedAtPath().desc()};
    }

    public Page<T> findActiveWithPaging(Pageable pageable) {
        long total = (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.getEntityPath().count()).from(this.getEntityPath())).where(this.isNotDeleted())).fetchOne();
        List<T> content = ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.getEntityPath()).where(this.isNotDeleted())).orderBy(this.getOrderSpecifiers(pageable))).offset(pageable.getOffset())).limit((long) pageable.getPageSize())).fetch();
        return new PageImpl(content, pageable, total);
    }

    public Page<T> findActiveByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable) {
        BooleanExpression condition = this.isNotDeleted().and(this.createdAtBetween(startDate, endDate));
        long total = (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.getEntityPath().count()).from(this.getEntityPath())).where(condition)).fetchOne();
        List<T> content = ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.getEntityPath()).where(condition)).orderBy(this.getOrderSpecifiers(pageable))).offset(pageable.getOffset())).limit((long) pageable.getPageSize())).fetch();
        return new PageImpl(content, pageable, total);
    }

    public long countActiveEntities() {
        return (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.getEntityPath().count()).from(this.getEntityPath())).where(this.isNotDeleted())).fetchOne();
    }

    public long countDeletedEntities() {
        return (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.getEntityPath().count()).from(this.getEntityPath())).where(this.isDeleted())).fetchOne();
    }

    public long countActiveByCreatedAtBetween(Instant startDate, Instant endDate) {
        return (Long) ((JPAQuery) ((JPAQuery) this.queryFactory.select(this.getEntityPath().count()).from(this.getEntityPath())).where(this.isNotDeleted().and(this.createdAtBetween(startDate, endDate)))).fetchOne();
    }

    protected BooleanExpression isNotDeleted() {
        return this.getIsDeletedPath().isFalse();
    }

    protected BooleanExpression isDeleted() {
        return this.getIsDeletedPath().isTrue();
    }

    protected BooleanExpression idEq(ID id) {
        return id != null ? this.getIdPath().eq(id) : null;
    }

    protected BooleanExpression createdAtBetween(Instant start, Instant end) {
        if (start == null && end == null) {
            return null;
        } else if (start == null) {
            return this.getCreatedAtPath().loe(end);
        } else {
            return end == null ? this.getCreatedAtPath().goe(start) : this.getCreatedAtPath().between(start, end);
        }
    }

    protected BooleanPath getIsDeletedPath() {
        return Expressions.booleanPath(this.getEntityPath(), "isDeleted");
    }

    protected DateTimePath<Instant> getCreatedAtPath() {
        return Expressions.dateTimePath(Instant.class, this.getEntityPath(), "createdAt");
    }

    protected DateTimePath<Instant> getUpdatedAtPath() {
        return Expressions.dateTimePath(Instant.class, this.getEntityPath(), "updatedAt");
    }

    protected DateTimePath<Instant> getDeletedAtPath() {
        return Expressions.dateTimePath(Instant.class, this.getEntityPath(), "deletedAt");
    }

    protected OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().isEmpty() ? this.getDefaultOrderSpecifiers() : (OrderSpecifier[]) pageable.getSort().stream().map(this::createOrderSpecifier).toArray((x$0) -> new OrderSpecifier[x$0]);
    }

    private OrderSpecifier<?> createOrderSpecifier(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;
        return new OrderSpecifier(direction, Expressions.path(Object.class, this.getEntityPath(), order.getProperty()));
    }

    @Generated
    public BaseQueryDslRepositoryImpl(final JPAQueryFactory queryFactory, final EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }
}
