package com.ritsard.baisard.domain.member.repository.projections;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MyCashiersProjection {
    UUID getCashierId();

    String getIdentifier();

    String getName();

    String getNickname();

    Boolean getIsActive();

    LocalDateTime getCreatedAt();
}
