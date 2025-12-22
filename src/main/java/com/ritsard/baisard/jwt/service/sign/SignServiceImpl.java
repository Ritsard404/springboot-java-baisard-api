package com.ritsard.baisard.jwt.service.sign;

import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.repository.login.LoginCredentialRepository;
import com.ritsard.baisard.jwt.repository.login.PermissionRepository;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import com.ritsard.baisard.utils.helper.AESConverter;
import com.ritsard.baisard.utils.log.LoggingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignServiceImpl<T extends BaseMember> implements SignService {
    private final BaseMemberRepository<T> baseMemberRepository;
    private final PermissionRepository permissionRepository;
    private final LoginCredentialRepository loginCredentialRepository;
    protected final AESConverter aesConverter;
    private final LoggingService loggingService;
    private final MemberRedisService memberRedisService;
    private final TokenProvider tokenProvider;
    private final AuthManager<T> authManager;
    @Override
    public <T extends BaseMember> void signup(SignupRequestDto dto, T baseMember, Set<String> permissionTypes) throws CryptoKeyException, EncryptionException {

    }

    @Override
    public void checkIdentifier(String identifier) {

    }

    @Override
    public void checkEmail(String email) {

    }

    @Override
    public String findIdentifierByNameAndEmail(String name, String email) {
        return "";
    }

    @Override
    public void validateResetPasswordTarget(String identifier, String email) {

    }

    @Override
    public void resetPassword(String identifier, String newPassword) {

    }

    @Override
    public void logout(HttpServletResponse response) {

    }

    @Override
    public void withdraw(UUID memberUuid, HttpServletResponse response) {

    }
}
