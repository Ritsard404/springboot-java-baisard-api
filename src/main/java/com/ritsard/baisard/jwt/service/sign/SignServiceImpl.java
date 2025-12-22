package com.ritsard.baisard.jwt.service.sign;

import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.model.entity.Permission;
import com.ritsard.baisard.jwt.model.enums.LoginType;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.repository.login.LoginCredentialRepository;
import com.ritsard.baisard.jwt.repository.login.PermissionRepository;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.DuplicateValueException;
import com.ritsard.baisard.utils.exceptions.IdentifierDuplicatedException;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import com.ritsard.baisard.utils.helper.AESConverter;
import com.ritsard.baisard.utils.helper.PasswordEncoderUtil;
import com.ritsard.baisard.utils.helper.UUIDManager;
import com.ritsard.baisard.utils.log.LoggingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional
    public <M extends BaseMember> void signup(
            SignupRequestDto dto,
            M baseMember,
            Set<String> permissionTypes
    ) throws CryptoKeyException, EncryptionException {

        validateDuplicate(dto);

        Set<Permission> permissions = resolvePermissions(permissionTypes);
        String encodedPassword = PasswordEncoderUtil.BCryptUtil.encode(dto.getPassword());

        LoginCredential credential = LoginCredential.builder()
                .uuidLoginCredential(UUIDManager.generateUUIDv7())
                .member(baseMember)
                .loginType(LoginType.GENERAL)
                .identifier(dto.getIdentifier())
                .password(encodedPassword)
                .build();

        baseMember.getLoginCredentials().add(credential);

        T savedBaseMember = registerBaseMember((T) baseMember, permissions, dto);
        baseMemberRepository.save(savedBaseMember);
    }

    private T registerBaseMember(
            T member,
            Set<Permission> permissions,
            SignupRequestDto dto
    ) throws CryptoKeyException, EncryptionException {

        member.setPermissions(permissions);

        String sanitizedPhone = sanitizePhone(dto.getPhoneNumber());
        String encryptedPhone = aesConverter.encryption(sanitizedPhone);

        member.setPhoneNumber(encryptedPhone);
        member.setEmail(dto.getEmail());
        member.setName(dto.getName());
        member.setNickname(dto.getNickname());
        member.setBirthdate(dto.getBirthDate());

        return member;
    }

    public void checkIdentifier(String identifier) {
        if (loginCredentialRepository.existsByIdentifier(identifier)) {
            throw new IdentifierDuplicatedException("This username is already in use.");
        }
    }

    public void checkEmail(String email) {
        if (baseMemberRepository.existsByEmail(email)) {
            throw new DuplicateValueException("This email is already registered.");
        }
    }

    public String findIdentifierByNameAndEmail(String name, String email) {
        return loginCredentialRepository
                .findIdentifierByNameAndEmail(name, email)
                .orElseThrow(() ->
                        new NoSuchUserException("No account is registered with the provided information.")
                );
    }

    public void validateResetPasswordTarget(String identifier, String email) {
        loginCredentialRepository
                .findByIdentifierAndEmail(identifier, email)
                .orElseThrow(() ->
                        new NoSuchUserException("No matching account was found.")
                );
    }

    @Transactional
    public void resetPassword(String identifier, String newPassword) {
        LoginCredential credential = loginCredentialRepository
                .findByIdentifier(identifier)
                .orElseThrow(() ->
                        new NoSuchUserException("The account does not exist.")
                );

        credential.setPassword(
                PasswordEncoderUtil.BCryptUtil.encode(newPassword)
        );
    }

    public void logout(HttpServletResponse response) {
        UUID memberUuid = authManager.getBaseMemberUuid();

        loggingService.logInfo("[Logout] uuidMember: " + memberUuid);
        tokenProvider.deleteRefreshTokenCookie(response);
        loggingService.logInfo("[Logout] Refresh token cookie deleted");
        loggingService.logInfo("[Logout] memberUuid=" + memberUuid);
    }

    @Transactional
    public void withdraw(UUID memberUuid, HttpServletResponse response) {

        BaseMember member = baseMemberRepository
                .findById(memberUuid)
                .orElseThrow(() ->
                        new NoSuchUserException("Member information could not be found.")
                );

        List<LoginCredential> credentials = member.getLoginCredentials();

        credentials.forEach(credential -> {
            if (!credential.isDeleted()) {
                credential.setDeleted(true);
                loggingService.logInfo(
                        "[LoginCredential deleted] type=" +
                                credential.getLoginType() +
                                ", id=" +
                                credential.getIdentifier()
                );
            }
        });

        if (!member.isDeleted()) {
            member.setDeleted(true);
            loggingService.logInfo("[BaseMember deleted] memberUuid=" + memberUuid);
        }

        memberRedisService.delete(memberUuid);
        loggingService.logInfo("[Redis deleted] memberUuid=" + memberUuid);

        tokenProvider.deleteRefreshTokenCookie(response);
        loggingService.logInfo("[Refresh token cookie deletion completed]");
    }

    private void validateDuplicate(SignupRequestDto dto)
            throws CryptoKeyException, EncryptionException {

        checkIdentifier(dto.getIdentifier());
        checkEmail(dto.getEmail());
        checkPhoneNumber(dto.getPhoneNumber());
    }

    private void checkPhoneNumber(String phoneNumber)
            throws CryptoKeyException, EncryptionException {

        String encryptedPhone = aesConverter.encryption(
                sanitizePhone(phoneNumber)
        );

        if (baseMemberRepository.existsByPhoneNumber(encryptedPhone)) {
            throw new DuplicateValueException("This phone number is already registered.");
        }
    }

    private Set<Permission> resolvePermissions(Set<String> types) {
        return types.stream()
                .map(type ->
                        permissionRepository
                                .findByPermissionType(type)
                                .orElseThrow(() ->
                                        new NoSuchElementException(
                                                "Permission does not exist: " + type
                                        )
                                )
                )
                .collect(Collectors.toSet());
    }

    private String sanitizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("[^0-9]", "");
    }
}
