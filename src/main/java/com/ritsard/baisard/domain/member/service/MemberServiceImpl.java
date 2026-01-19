package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.dto.response.MemberListDto;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.projections.MemberListProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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
}
