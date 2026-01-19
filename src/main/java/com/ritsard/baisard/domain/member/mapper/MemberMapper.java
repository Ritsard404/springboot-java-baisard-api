package com.ritsard.baisard.domain.member.mapper;

import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.dto.response.MemberListDto;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.jwt.model.entity.Permission;

import java.util.stream.Collectors;

public class MemberMapper {
//    public static MemberListDto toDto(Member member) {
//        return MemberListDto.builder()
//                .memberId(member.getId())
//                .identifier(member.getIdentifier())
//                .classification(member.getClassification())
//                .approvalStatus(member.getApprovalStatus().name())
//                .company(CompanyDto.builder()
//                        .uuid(member.getCompany() != null ? member.getCompany().getUuidCompany() : null)
//                        .code(member.getCompany() != null ? member.getCompany().getCode() : null)
//                        .name(member.getCompany() != null ? member.getCompany().getName() : null)
//                        .build())
//                .permissions(member.getPermissions().stream()
//                        .map(Permission::getPermissionType)
//                        .collect(Collectors.toList()))
//                .build();
//    }
}
