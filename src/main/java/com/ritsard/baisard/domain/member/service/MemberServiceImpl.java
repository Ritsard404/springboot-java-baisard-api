package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.response.*;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.mapper.MemberMapper;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.projections.MemberListProjection;
import com.ritsard.baisard.domain.member.repository.projections.MyCashiersProjection;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import com.ritsard.baisard.utils.helper.AESConverter;
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

    private final MemberRepository memberRepository;
    private final AuthManager<Member> authManager;
    private final AESConverter aesConverter;

    @Override
    public Page<MemberListDto> getMembers(String keyword, MemberApprovalStatus approvalStatus, UUID companyId, Integer page, Integer size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                direction != null && direction.equalsIgnoreCase("desc") ?
                        Sort.by(sortBy).descending() :
                        Sort.by(sortBy).ascending()
        );

        Page<MemberListProjection> projectionPage =
                memberRepository.findMembersWithProjection(keyword, approvalStatus, companyId, pageable);

        List<MemberListDto> dtoList = projectionPage.stream()
                .map(p -> MemberListDto.builder()
                        .memberId(p.getMemberId())
                        .identifier(p.getIdentifier())
                        .approvalStatus(p.getApprovalStatus())
                        .company(CompanyDto.builder()
                                .uuid(p.getCompanyId())
                                .code(p.getCompanyCode())
                                .name(p.getCompanyName())
                                .build())
                        .permissions(p.getPermissions())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, projectionPage.getTotalElements());
    }

    @Override
    public AdminInfoDto adminProfile() {
        Member member = authManager.getMember();
        return MemberMapper.toAdminDto(member);
    }

    @Override
    public void updateAdminProfile(AdminInfoDto adminInfoDto) {
// 1. In a real scenario, you'd get the current Admin's UUID from the AuthManager
        UUID adminUuid = authManager.getBaseMemberUuid();

        Member admin = memberRepository.findById(adminUuid)
                .orElseThrow(() -> new NoSuchUserException("Admin not found"));

        // 2. Map changes (Passing aesConverter for the phone number)
        MemberMapper.updateAdminFromDto(adminInfoDto, admin, aesConverter);
    }

    @Override
    public void approveMember(UUID memberId) {
//        Member approver = authManager.getMember();
//
//        boolean isSuperAdmin = approver.getClassification() == PermissionType.SUPERADMIN.getDescription();
//
//        if (!isSuperAdmin)
//            throw new SecurityException("Only SUPERADMIN can approve members");


//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new NotFoundException("Member not found"));
//
//        member.approve(approver.getId());
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
