package com.ritsard.baisard.jwt.repository.member;

import com.ritsard.baisard.base.repository.BaseRepository;
import com.ritsard.baisard.jwt.model.entity.BaseMember;

import java.util.Optional;
import java.util.UUID;

public interface BaseMemberRepository<T extends BaseMember> extends BaseRepository<T, UUID>, BaseMemberRepositoryCustom<T> {
    Optional<T> findByEmail(String email);

    Optional<T> findByEmailAndIsDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIsDeletedFalse(String nickname);
}