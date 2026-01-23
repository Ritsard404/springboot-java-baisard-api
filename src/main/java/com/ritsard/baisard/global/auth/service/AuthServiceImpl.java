package com.ritsard.baisard.global.auth.service;

import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.enums.PermissionType;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.repository.CompanyRepository;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.global.auth.dto.request.SignUpAdminDto;
import com.ritsard.baisard.global.auth.dto.request.UpdateMemberPasswordDto;
import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.repository.login.LoginCredentialRepository;
import com.ritsard.baisard.jwt.repository.login.PermissionRepository;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.service.login.LoginService;
import com.ritsard.baisard.jwt.service.sign.SignServiceImpl;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import com.ritsard.baisard.utils.helper.AESConverter;
import com.ritsard.baisard.utils.log.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Primary
@Transactional
@Slf4j
public class AuthServiceImpl extends SignServiceImpl<Member> implements AuthService {

    private final LoginService loginService;
    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;

    public AuthServiceImpl(
            BaseMemberRepository<Member> baseMemberRepository,
            PermissionRepository permissionRepository,
            LoginCredentialRepository loginCredentialRepository,
            AESConverter aesConverter,
            LoggingService loggingService,
            MemberRedisService memberRedisService,
            TokenProvider tokenProvider,
            AuthManager<Member> authManager, LoginService loginService, CompanyRepository companyRepository, MemberRepository memberRepository) {
        super(baseMemberRepository, permissionRepository, loginCredentialRepository,
                aesConverter, loggingService, memberRedisService, tokenProvider, authManager);
        this.loginService = loginService;
        this.companyRepository = companyRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public LoginResponseDto loginMember(LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        LoginCredential credential = loginCredentialRepository
                .findByIdentifier(dto.getIdentifier())
                .orElseThrow(() ->
                        new NoSuchUserException("Invalid username or password."));

        UUID memberId = credential.getMember().getUuidMember();

        // Fetch the actual Member entity, not BaseMember
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NoSuchUserException("User account does not exist."));

        if (!member.isActive())
            throw new NoSuchUserException("This account has been deactivated.");


        if (!member.isApproved())
            throw new NoSuchUserException(
                    "Your account is pending approval by a system administrator."
            );


        return loginService.login(dto, request, response);

    }

    @Override
    public void registerAdmin(SignUpAdminDto dto) throws CryptoKeyException, EncryptionException {

        Company company = Company.builder()
                .name(dto.getCompanyName())
                .code(dto.getCompanyCode())
                .phone(dto.getPhoneNumber())
                .email(dto.getEmail())
                .approved(false)
                .build();

        Company savedCompany = companyRepository.save(company);

        Member member = Member.builder()
                .company(savedCompany)
                .approvalStatus(MemberApprovalStatus.PENDING)
                .build();

        signup(dto, member, Set.of(PermissionType.ADMIN.toString()));
    }

    @Override
    public void registerSuperAdmin(SignupRequestDto signupRequestDto) throws CryptoKeyException, EncryptionException {
        Member member = Member.builder()
                .approvalStatus(MemberApprovalStatus.APPROVED)
                .build();
        signup(signupRequestDto, member, Set.of(PermissionType.SUPERADMIN.toString()));
    }

    @Override
    public void registerCashier(SignupRequestDto signupRequestDto) throws CryptoKeyException, EncryptionException {
        Member admin = authManager.getMember();
        Member member = Member.builder()
                .company(admin.getCompany())
                .approvalStatus(MemberApprovalStatus.APPROVED)
                .build();
        signup(signupRequestDto, member, Set.of(PermissionType.CASHIER.toString()));
    }

    @Override
    public void resetPassword(UpdateMemberPasswordDto dto) {
        validateResetPasswordTarget(dto.getIdentifier(), dto.getEmail());
        resetPassword(dto.getIdentifier(), dto.getPassword());
    }

}
