package com.ritsard.baisard.domain.member.repository;

import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
