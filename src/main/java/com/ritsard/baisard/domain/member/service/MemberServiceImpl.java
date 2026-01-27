package com.ritsard.baisard.domain.member.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.domain.member.dto.response.MemberInfoDto;
import com.ritsard.baisard.domain.member.dto.response.MemberListDto;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.QCompany;
import com.ritsard.baisard.domain.member.entity.QMember;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.mapper.CompanyMapper;
import com.ritsard.baisard.domain.member.mapper.MemberMapper;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.global.utils.AESUtil;
import com.ritsard.baisard.jwt.model.entity.QLoginCredential;
import com.ritsard.baisard.jwt.model.entity.QPermission;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final CompanyMapper companyMapper;
    private final MemberRepository memberRepository;
    private final AESUtil aesUtil;
    private final AuthManager<Member> authManager;

    @Override
    public Page<MemberListDto> getMembers(String keyword, MemberApprovalStatus approvalStatus, Integer page, Integer size, String sortBy, String direction) {
        QMember member = QMember.member;
        QLoginCredential login = QLoginCredential.loginCredential;
        QCompany company = QCompany.company;
        QPermission permission = QPermission.permission; // Need the Q class for permission


        Member currentUser = authManager.getMember();
        // 1. Build Predicate
        BooleanBuilder where = new BooleanBuilder();
//        where.and(member.memberIsDeleted.isFalse());
        where.and(member.uuidMember.ne(currentUser.getUuidMember()));

        if (approvalStatus != null) where.and(member.approvalStatus.eq(approvalStatus));
        if (keyword != null && !keyword.isBlank()) {
            where.and(member.name.containsIgnoreCase(keyword)
//                    .or(login.identifier.containsIgnoreCase(keyword))
                    .or(member.company.name.containsIgnoreCase(keyword))
                    .or(company.name.containsIgnoreCase(keyword)));
        }

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;


        // We join permissions to get the type directly
        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(
                        member.uuidMember,
                        login.identifier,
                        member.approvalStatus,
                        company.uuidCompany,
                        company.code,
                        company.name,
                        permission.permissionType
                )
                .from(member)
                // FIX: You must join via the property path in BaseMember
                .leftJoin(member.loginCredentials, login)
                .leftJoin(member.company, company)
                .leftJoin(member.permissions, permission)
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
    public MemberInfoDto getMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return MemberMapper.toMemberInfoDto(member, aesUtil, companyMapper);
    }

    @Override
    public void updateMemberInfo(MemberInfoDto memberInfoDto, UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        MemberMapper.updateMemberEntity(member, memberInfoDto, aesUtil);
        memberRepository.save(member);
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
