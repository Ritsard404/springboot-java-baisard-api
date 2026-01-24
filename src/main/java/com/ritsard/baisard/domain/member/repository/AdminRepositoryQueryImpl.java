package com.ritsard.baisard.domain.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import com.ritsard.baisard.domain.member.dto.response.QMyCashiersDto;
import com.ritsard.baisard.domain.member.entity.QMember;
import com.ritsard.baisard.jwt.model.entity.QLoginCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryQueryImpl implements AdminRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MyCashiersDto> findMyCashiersProjected(BooleanBuilder where, Pageable pageable) {
        QMember member = QMember.member;
        QLoginCredential login = QLoginCredential.loginCredential;

        // Use the Q-Class generated from your @QueryProjection
        List<MyCashiersDto> content = queryFactory
                .select(new QMyCashiersDto(
                        member.uuidMember,
                        login.identifier,
                        member.name,
                        member.nickname,
                        member.memberIsDeleted.not()
                ))
                .from(member)
                // We join loginCredentials to get the identifier directly in the select
                .leftJoin(member.loginCredentials, login)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                // Simple sorting logic for native QueryDSL
                .orderBy(member.createdAt.desc())
                .fetch();

        long total = queryFactory.select(member.count()).from(member).where(where).fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
