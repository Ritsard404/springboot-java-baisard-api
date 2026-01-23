package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.response.MemberListDto;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface MemberService {

    Page<MemberListDto> getMembers(
            String keyword,
            MemberApprovalStatus approvalStatus,
            UUID companyId,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    );


    void approveMember(UUID memberId);

    void activateMember(UUID memberId);

    void deActivateMember(UUID memberId);
}
