package com.ritsard.baisard.jwt.repository.login;

import org.springframework.stereotype.Service;

@Service
public class LoginCredentialRepositoryImpl  {
//public class LoginCredentialRepositoryImpl implements LoginCredentialRepositoryCustom {
//    private final JPAQueryFactory queryFactory;
//    private final QLoginCredential c;
//    private final QBaseMember m;
//
//    public Optional<LoginCredential> findWithMember(UUID uuidLoginCredential) {
//        return Optional.ofNullable((LoginCredential)((JPAQuery)((JPAQuery)((JPAQuery)this.queryFactory.selectFrom(this.c).leftJoin(this.c.member, this.m)).fetchJoin()).where(new Predicate[]{this.c.uuidLoginCredential.eq(uuidLoginCredential), this.c.isDeleted.isFalse(), this.m.isDeleted.isFalse()})).fetchOne());
//    }
//
//    public Optional<LoginCredential> findWithMemberByIdentifier(String identifier) {
//        return Optional.ofNullable((LoginCredential)((JPAQuery)((JPAQuery)((JPAQuery)this.queryFactory.selectFrom(this.c).leftJoin(this.c.member, this.m)).fetchJoin()).where(new Predicate[]{this.c.identifier.eq(identifier), this.c.isDeleted.isFalse(), this.m.isDeleted.isFalse()})).fetchOne());
//    }
//
//    @Generated
//    public LoginCredentialRepositoryImpl(final JPAQueryFactory queryFactory) {
//        this.c = QLoginCredential.loginCredential;
//        this.m = QBaseMember.baseMember;
//        this.queryFactory = queryFactory;
//    }
}