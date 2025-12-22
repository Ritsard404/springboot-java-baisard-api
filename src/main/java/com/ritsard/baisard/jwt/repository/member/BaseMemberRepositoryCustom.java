package com.ritsard.baisard.jwt.repository.member;

import com.ritsard.baisard.base.repository.BaseQueryDslRepository;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BaseMemberRepositoryCustom<T extends BaseMember> extends BaseQueryDslRepository<T, UUID> {
    Optional<T> findWithDetailsByIdentifier(String identifier);

    Optional<T> findWithPermissionsByUuid(UUID uuid);

    Optional<T> findWithCredentialsByUuid(UUID uuid);

    Optional<T> findWithCredentialsByIdentifier(String identifier);

    Optional<T> findWithAllDetailsByUuid(UUID uuid);

    Page<T> findActiveByNameContaining(String name, Pageable pageable);
}
