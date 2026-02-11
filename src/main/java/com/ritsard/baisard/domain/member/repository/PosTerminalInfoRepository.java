package com.ritsard.baisard.domain.member.repository;

import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PosTerminalInfoRepository extends JpaRepository<PosTerminalInfo, UUID> {
    Page<PosTerminalInfo> findAllByCompanyUuidCompany(UUID uuidCompany, Pageable pageable);
}
