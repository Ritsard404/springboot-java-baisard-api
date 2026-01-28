package com.ritsard.baisard.domain.member.dto.response;


import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
//@Builder
public class MemberListDto {

    private UUID memberId;
    private String identifier;
    private MemberApprovalStatus approvalStatus;
    private Boolean isActive;

    private CompanyDto company;
    private String permission;

    public MemberListDto(
            UUID memberId,
            String identifier,
            MemberApprovalStatus approvalStatus, // Use Object or Enum type depending on your entity
            Boolean isDeleted,
            UUID companyUuid,
            String companyCode,
            String companyName,
            String permission
    ) {
        this.memberId = memberId;
        this.identifier = identifier;
        this.approvalStatus = approvalStatus;
        this.isActive = !isDeleted;
        this.company = CompanyDto.builder()
                .uuid(companyUuid)
                .code(companyCode)
                .name(companyName)
                .build();
        this.permission = permission;
    }
}