package com.ritsard.baisard.domain.member.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.domain.member.dto.response.*;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.QCompany;
import com.ritsard.baisard.domain.member.entity.QMember;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.jwt.model.entity.QLoginCredential;
import com.ritsard.baisard.jwt.model.entity.QPermission;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final AuthManager<Member> authManager;

    @Override
    public Page<MemberListDto> getMembers(String keyword, MemberApprovalStatus approvalStatus, UUID companyId, Integer page, Integer size, String sortBy, String direction) {
        QMember member = QMember.member;
        QLoginCredential login = QLoginCredential.loginCredential;
        QCompany company = QCompany.company;
        QPermission permission = QPermission.permission; // Need the Q class for permission

        // 1. Build Predicate
        BooleanBuilder where = new BooleanBuilder();
        where.and(member.memberIsDeleted.isFalse());

        if (approvalStatus != null) where.and(member.approvalStatus.eq(approvalStatus));
        if (companyId != null) where.and(member.company.uuidCompany.eq(companyId));
        if (keyword != null && !keyword.isBlank()) {
            where.and(member.name.containsIgnoreCase(keyword)
                    .or(login.identifier.containsIgnoreCase(keyword))
                    .or(company.name.containsIgnoreCase(keyword)));
        }

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        // 2. Fetch as Tuples
        // We join permissions to get the type directly
        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(
                        member.uuidMember,
                        login.identifier,
                        member.approvalStatus,
                        company.uuidCompany,
                        company.code,
                        company.name,
                        permission.permissionType // Select the actual string field
                )
                .from(member)
                .leftJoin(member.loginCredentials, login)
                .leftJoin(member.company, company)
                .leftJoin(member.permissions, permission) // Important: Join the ManyToMany
                .where(where)
                .offset((long) pageNum * pageSize)
                .limit(pageSize)
                .orderBy("desc".equalsIgnoreCase(direction) ? member.createdAt.desc() : member.createdAt.asc())
                .fetch();

        // 3. Manual Mapping to your new Constructor
        List<MemberListDto> content = tuples.stream().map(tuple ->
                new MemberListDto(
                        tuple.get(member.uuidMember),
                        tuple.get(login.identifier),
                        tuple.get(member.approvalStatus),
                        tuple.get(company.uuidCompany),
                        tuple.get(company.code),
                        tuple.get(company.name),
                        tuple.get(permission.permissionType) // Maps to 'String permission' in DTO
                )
        ).collect(Collectors.toList());

        // 4. Total Count
        long total = queryFactory.select(member.count()).from(member).where(where).fetchOne();

        return new PageImpl<>(content, PageRequest.of(pageNum, pageSize), total);
    }

    @Override
    public void approveMember(UUID memberId) {
        updateMember(memberId, member -> member.approve(authManager.getMember()));
    }

    @Override
    public void activateMember(UUID memberId) {
        updateMember(memberId, Member::restoreMember);
    }

    @Override
    public void deActivateMember(UUID memberId) {
        updateMember(memberId, Member::softDelete);
    }

    private void updateMember(UUID memberId, Consumer<Member> action) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));

        action.accept(member);

        memberRepository.save(member);
    }
}
