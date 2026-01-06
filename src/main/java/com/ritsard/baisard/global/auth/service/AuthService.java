package com.ritsard.baisard.global.auth.service;

import com.ritsard.baisard.domain.member.enums.PermissionType;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.repository.login.LoginCredentialRepository;
import com.ritsard.baisard.jwt.repository.login.PermissionRepository;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.service.sign.SignServiceImpl;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import com.ritsard.baisard.utils.helper.AESConverter;
import com.ritsard.baisard.utils.log.LoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Primary
@Transactional
@Slf4j
public class AuthService extends SignServiceImpl<Member> {

    public AuthService(
            BaseMemberRepository<Member> baseMemberRepository,
            PermissionRepository permissionRepository,
            LoginCredentialRepository loginCredentialRepository,
            AESConverter aesConverter,
            LoggingService loggingService,
            MemberRedisService memberRedisService,
            TokenProvider tokenProvider,
            AuthManager<Member> authManager) {
        super(baseMemberRepository, permissionRepository, loginCredentialRepository,
                aesConverter, loggingService, memberRedisService, tokenProvider, authManager);
    }

    public void registerAdmin(SignupRequestDto signupRequestDto) throws CryptoKeyException, EncryptionException {
        Member member = new Member();
        signup(signupRequestDto, member, Set.of(PermissionType.ADMIN.toString()));
    }

    public void registerSuperAdmin(SignupRequestDto signupRequestDto) throws CryptoKeyException, EncryptionException {
        Member member = new Member();
        signup(signupRequestDto, member, Set.of(PermissionType.SUPERADMIN.toString()));
    }

    public void registerCashier(SignupRequestDto signupRequestDto) throws CryptoKeyException, EncryptionException {
        Member member = new Member();
        signup(signupRequestDto, member, Set.of(PermissionType.CASHIER.toString()));
    }

}
