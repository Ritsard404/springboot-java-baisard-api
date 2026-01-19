package com.ritsard.baisard.domain.member.repository.projections;

import java.util.List;
import java.util.UUID;

public interface MemberListProjection {

    UUID getMemberId();

    String getIdentifier();

    String getApprovalStatus();

    UUID getCompanyId();

    String getCompanyCode();

    String getCompanyName();

    List<String> getPermissions();
}
