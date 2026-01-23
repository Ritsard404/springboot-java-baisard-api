package com.ritsard.baisard.domain.member.service;

import com.querydsl.core.BooleanBuilder;
import com.ritsard.baisard.domain.member.dto.request.UpdateCompanyDto;
import com.ritsard.baisard.domain.member.dto.response.AdminInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.QMember;
import com.ritsard.baisard.domain.member.enums.PermissionType;
import com.ritsard.baisard.domain.member.mapper.CompanyMapper;
import com.ritsard.baisard.domain.member.mapper.MemberMapper;
import com.ritsard.baisard.domain.member.repository.CompanyRepository;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.member.repository.projections.MyCashiersProjection;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.helper.AESConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final MemberRepository memberRepository;
    private final AuthManager<Member> authManager;
    private final PosTerminalInfoRepository posTerminalInfoRepository;
    private final CompanyRepository companyRepository;
    private final AESConverter aesConverter;
    private final CompanyMapper companyMapper;

    @Override
    public AdminInfoDto adminProfile() {
        Member member = authManager.getMember();
        return MemberMapper.toAdminDto(member);
    }

    @Override
    public void updateAdminProfile(AdminInfoDto adminInfoDto) {
// 1. In a real scenario, you'd get the current Admin's UUID from the AuthManager
        UUID adminUuid = authManager.getBaseMemberUuid();

        Member admin = memberRepository.findById(adminUuid)
                .orElseThrow(() -> new NoSuchUserException("Admin not found"));

        // 2. Map changes (Passing aesConverter for the phone number)
        MemberMapper.updateAdminFromDto(adminInfoDto, admin, aesConverter);
    }

    @Override
    public CashierInfoDto cashierInfo(UUID cashierId) {
        Member member = memberRepository.findById(cashierId)
                .orElseThrow(() -> new NoSuchUserException("Cashier not found!"));
        return MemberMapper.toCashierDto(member);
    }

    @Override
    public void updateCashierInfo(CashierInfoDto dto) {
        Member member = memberRepository.findById(dto.getCashierId())
                .orElseThrow(() -> new NoSuchUserException("Cashier not found with ID: " + dto.getCashierId()));

        MemberMapper.updateCashierFromDto(dto, member);
    }

    @Override
    public CompanyDto companyInfo() {
        Company adminsCompany = authManager.getMember().getCompany();
        return companyMapper.toCompanyDto(adminsCompany);
    }

    @Override
    public void updateCompany(UpdateCompanyDto dto) {
        Company company = authManager.getMember().getCompany();
        companyMapper.toCompany(dto);
        companyRepository.save(company);
    }

    @Override
    public Page<MyCashiersDto> myCashiers(String keyword, Integer page, Integer size, String sortBy, String direction) {
        QMember member = QMember.member;

        // 1. Context Check
        Member admin = authManager.getMember();
        if (admin.getCompany() == null)
            throw new ConflictException("Admin is not associated with any company.");

        UUID companyId = admin.getCompany().getUuidCompany();

        // 2. Build Predicate (The "LINQ" way)
        BooleanBuilder where = new BooleanBuilder();

        // Filter by Company and PermissionType
        where.and(member.company.uuidCompany.eq(companyId));
        where.and(member.permissions.any().permissionType.eq(PermissionType.CASHIER.name()));

        // Optional Keyword Search
        if (keyword != null && !keyword.isBlank()) {
            where.and(
                    member.name.containsIgnoreCase(keyword)
                            .or(member.loginCredentials.any().identifier.containsIgnoreCase(keyword))
            );
        }

        // 3. Setup Pageable
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        // 4. Execute and Map (Using the mapper you already have)
        return memberRepository.findAll(where, pageable)
                .map(MemberMapper::toMyCashiersDto);
    }
}
