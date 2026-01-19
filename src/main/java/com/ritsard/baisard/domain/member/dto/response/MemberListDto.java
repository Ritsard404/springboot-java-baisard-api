package com.ritsard.baisard.domain.member.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MemberListDto {

    private UUID memberId;
    private String identifier;
    private String approvalStatus;

    private CompanyDto company;
    private List<String> permissions;

//    public MemberListDto(
//            UUID memberId,
//            String identifier,
//            String approvalStatus,
//            UUID companyUuid,
//            String companyCode,
//            String companyName,
//            List<String> permissions
//    ) {
//        this.memberId = memberId;
//        this.identifier = identifier;
//        this.approvalStatus = approvalStatus;
//        this.company.setUuid(companyUuid);
//        this.company.setCode(companyCode);
//        this.company.setName(companyName);
//        this.permissions = permissions;
//    }
}