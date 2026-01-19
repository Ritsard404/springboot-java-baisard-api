package com.ritsard.baisard.domain.member.repository;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.repository.projections.MemberListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    @Query("""
        SELECT DISTINCT m.uuidMember as memberId,
                        lc.identifier as identifier,
                        m.approvalStatus as approvalStatus,
                        c.id as companyId,
                        c.code as companyCode,
                        c.name as companyName,
                        p.permissionType as permissions
        FROM Member m
        LEFT JOIN m.loginCredentials lc
        LEFT JOIN m.permissions p
        LEFT JOIN m.company c
        WHERE (:approvalStatus IS NULL OR m.approvalStatus = :approvalStatus)
          AND (:companyId IS NULL OR c.id = :companyId)
          AND (
                :keyword IS NULL 
                OR LOWER(CAST(lc.identifier AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
        ORDER BY
            CASE WHEN :keyword IS NULL THEN 1
                 WHEN LOWER(CAST(lc.identifier AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 0 
                 ELSE 1 
            END,
            lc.identifier ASC
    """)
    Page<MemberListProjection> findMembersWithProjection(
            @Param("keyword") String keyword,
            @Param("approvalStatus") MemberApprovalStatus approvalStatus,
            @Param("companyId") UUID companyId,
            Pageable pageable
    );
}