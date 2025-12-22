package com.ritsard.baisard.jwt.repository.login;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.jwt.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.Predicate;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LoginCredentialRepositoryImpl implements LoginCredentialRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QLoginCredential c = QLoginCredential.loginCredential;
    private static final QBaseMember m = QBaseMember.baseMember;

    public LoginCredentialRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<LoginCredential> findWithMember(UUID uuidLoginCredential) {
        return Optional.ofNullable(queryFactory
                .selectFrom(c)
                .leftJoin(c.member, m).fetchJoin()
                .where(c.uuidLoginCredential.eq(uuidLoginCredential)
                        .and(c.isDeleted.isFalse())
                        .and(m.isDeleted.isFalse()))
                .fetchOne());
    }

    public Optional<LoginCredential> findWithMemberByIdentifier(String identifier) {
        return Optional.ofNullable(queryFactory
                .selectFrom(c)
                .leftJoin(c.member, m).fetchJoin()
                .where(c.identifier.eq(identifier)
                        .and(c.isDeleted.isFalse())
                        .and(m.isDeleted.isFalse()))
                .fetchOne());
    }
}
