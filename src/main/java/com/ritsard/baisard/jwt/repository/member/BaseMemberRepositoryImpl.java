package com.ritsard.baisard.jwt.repository.member;

import com.ritsard.baisard.base.repository.BaseQueryDslRepositoryImpl;
import com.ritsard.baisard.base.repository.BaseSearchCondition;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.model.entity.QBaseMemberManual;
import com.ritsard.baisard.jwt.model.entity.QLoginCredentialManual;
import com.ritsard.baisard.jwt.model.entity.QPermissionManual;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
@Transactional(
        readOnly = true
)
public class BaseMemberRepositoryImpl<T extends BaseMember> extends BaseQueryDslRepositoryImpl<T, UUID> implements BaseMemberRepositoryCustom<T> {
    private final QBaseMemberManual member;
    private final QLoginCredentialManual credential;
    private final QPermissionManual permission;

    public BaseMemberRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
        this.member = QBaseMemberManual.baseMember;
        this.credential = QLoginCredentialManual.loginCredential;
        this.permission = QPermissionManual.permission;
    }

    protected EntityPathBase<T> getEntityPath() {
        return (EntityPathBase<T>) this.member;
    }

    protected SimpleExpression<UUID> getIdPath() {
        return this.member.uuidMember;
    }

    protected Class<T> getEntityClass() {
        return (Class<T>) BaseMember.class;
    }

    protected void addDynamicConditions(BooleanBuilder builder, Object searchCondition) {
        if (searchCondition instanceof BaseSearchCondition baseCondition) {
            builder.and(this.keywordContains(baseCondition.getKeyword()));
            builder.and(this.createdAtBetween(baseCondition.getStartDate(), baseCondition.getEndDate()));
            if (Boolean.TRUE.equals(baseCondition.getIncludeDeleted())) {
                builder.or(this.isDeleted());
            }
        }

        if (searchCondition instanceof BaseMemberSearchCondition memberCondition) {
            builder.and(this.emailContains(memberCondition.getEmail())).and(this.nameContains(memberCondition.getName())).and(this.nicknameContains(memberCondition.getNickname())).and(this.phoneNumberContains(memberCondition.getPhoneNumber())).and(this.birthdateBetween(memberCondition.getBirthdateStart(), memberCondition.getBirthdateEnd())).and(this.hasPermissions(memberCondition.getHasPermissions())).and(this.hasPermissionName(memberCondition.getPermissionName()));
        }

    }

    private BooleanExpression emailContains(String email) {
        return StringUtils.hasText(email) ? this.member.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? this.member.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? this.member.nickname.containsIgnoreCase(nickname) : null;
    }

    private BooleanExpression phoneNumberContains(String phoneNumber) {
        return StringUtils.hasText(phoneNumber) ? this.member.phoneNumber.containsIgnoreCase(phoneNumber) : null;
    }

    private BooleanExpression birthdateBetween(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return null;
        } else if (start == null) {
            return this.member.birthdate.loe(end);
        } else {
            return end == null ? this.member.birthdate.goe(start) : this.member.birthdate.between(start, end);
        }
    }

    private BooleanExpression hasPermissions(Boolean hasPermissions) {
        if (hasPermissions == null) {
            return null;
        } else {
            return hasPermissions ? this.member.permissions.isNotEmpty() : this.member.permissions.isEmpty();
        }
    }

    private BooleanExpression hasPermissionName(String permissionName) {
        return StringUtils.hasText(permissionName) ? ((QPermissionManual) this.member.permissions.any()).permissionType.eq(permissionName) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? this.member.name.containsIgnoreCase(keyword).or(this.member.email.containsIgnoreCase(keyword)).or(this.member.nickname.containsIgnoreCase(keyword)) : null;
    }

    public Optional<T> findWithDetailsByIdentifier(String identifier) {
        T result = (T) (((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.member).leftJoin(this.member.permissions, this.permission)).fetchJoin()).leftJoin(this.member.loginCredentials, this.credential)).fetchJoin()).where(this.credential.identifier.eq(identifier).and(this.credential.isDeleted.isFalse()).and(this.isNotDeleted()))).fetchOne());
        return Optional.ofNullable(result);
    }

    public Optional<T> findWithPermissionsByUuid(UUID uuid) {
        T result = (T) (((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.member).leftJoin(this.member.permissions, this.permission)).fetchJoin()).where(this.member.uuidMember.eq(uuid).and(this.isNotDeleted()))).fetchOne());
        return Optional.ofNullable(result);
    }

    public Optional<T> findWithCredentialsByUuid(UUID uuid) {
        T result = (T) (((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.member).leftJoin(this.member.loginCredentials, this.credential)).fetchJoin()).where(this.member.uuidMember.eq(uuid).and(this.isNotDeleted()).and(this.credential.isDeleted.isFalse()))).fetchOne());
        return Optional.ofNullable(result);
    }

    public Optional<T> findWithCredentialsByIdentifier(String identifier) {
        T result = (T) (((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.member).leftJoin(this.member.loginCredentials, this.credential)).fetchJoin()).where(this.credential.identifier.eq(identifier).and(this.credential.isDeleted.isFalse()).and(this.isNotDeleted()))).fetchOne());
        return Optional.ofNullable(result);
    }

    public Optional<T> findWithAllDetailsByUuid(UUID uuid) {
        T result = (T) (((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) ((JPAQuery) this.queryFactory.selectFrom(this.member).leftJoin(this.member.permissions, this.permission)).fetchJoin()).leftJoin(this.member.loginCredentials, this.credential)).fetchJoin()).where(this.member.uuidMember.eq(uuid).and(this.isNotDeleted()).and(this.credential.isDeleted.isFalse()))).fetchOne());
        return Optional.ofNullable(result);
    }

    public Page<T> findActiveByNameContaining(String name, Pageable pageable) {
        BaseMemberSearchCondition condition = BaseMemberSearchCondition.builder().name(name).build();
        return this.findByConditions(condition, pageable);
    }
}
