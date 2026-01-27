package com.ritsard.baisard.domain.member.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import com.ritsard.baisard.domain.member.repository.AdminRepositoryQuery;
import com.ritsard.baisard.domain.member.repository.CompanyRepository;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.global.utils.AESUtil;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import com.ritsard.baisard.utils.helper.AESConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final MemberRepository memberRepository;
    private final AuthManager<Member> authManager;
    private final PosTerminalInfoRepository posTerminalInfoRepository;
    private final JPAQueryFactory queryFactory;
    private final CompanyRepository companyRepository;
    private final AESConverter aesConverter;
    private final CompanyMapper companyMapper;
    private final AdminRepositoryQuery adminRepositoryQuery;
    private final AESUtil aesUtil;

    @Override
    public AdminInfoDto adminProfile(UUID adminId) {
        Member member = authManager.getMember();

        // Logged-in user is ADMIN → use session user
        if (PermissionType.ADMIN.equals(member.getClassification()))
            return MemberMapper.toAdminDto(member, aesUtil);


        // Not ADMIN → fetch by provided ID
        Member target = memberRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchUserException("User not found"));

        return MemberMapper.toAdminDto(target, aesUtil);
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
        if (company == null)
            throw new NotFoundException("No company associated with this member");

        // Update the existing managed entity
        companyMapper.updateCompanyFromDto(dto, company);
        companyRepository.save(company);
    }

    @Override
    public Page<MyCashiersDto> myCashiers(String keyword, Integer page, Integer size, String sortBy, String direction) {
        QMember member = QMember.member;

        // 1. Business Logic / Context
        Member admin = authManager.getMember();
        if (admin.getCompany() == null)
            throw new ConflictException("Admin is not associated with any company.");

        UUID companyId = admin.getCompany().getUuidCompany();

        // 2. Build Search Predicate
        BooleanBuilder where = new BooleanBuilder();
        where.and(member.company.uuidCompany.eq(companyId));
        where.and(member.permissions.any().permissionType.eq(PermissionType.CASHIER.name()));

        if (keyword != null && !keyword.isBlank()) {
            where.and(
                    member.name.containsIgnoreCase(keyword)
                            .or(member.loginCredentials.any().identifier.containsIgnoreCase(keyword))
            );
        }

        // 3. Setup Pagination
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        // 4. Call Custom Repository Projection
        return adminRepositoryQuery.findMyCashiersProjected(where, pageable);
    }
}
