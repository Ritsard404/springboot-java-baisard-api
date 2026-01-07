package com.ritsard.baisard.domain.member.repository;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
}
