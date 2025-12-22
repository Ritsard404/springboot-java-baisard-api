package com.ritsard.baisard.jwt.service.sign;

import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;
import java.util.UUID;

public interface SignService {
    <T extends BaseMember> void signup(SignupRequestDto dto, T baseMember, Set<String> permissionTypes) throws CryptoKeyException, EncryptionException;

    void checkIdentifier(String identifier);

    void checkEmail(String email);

    String findIdentifierByNameAndEmail(String name, String email);

    void validateResetPasswordTarget(String identifier, String email);

    void resetPassword(String identifier, String newPassword);

    void logout(HttpServletResponse response);

    void withdraw(UUID memberUuid, HttpServletResponse response);
}
