package com.ritsard.baisard.global.auth.service;

import com.ritsard.baisard.global.auth.dto.request.SignUpAdminDto;
import com.ritsard.baisard.global.auth.dto.request.SignupRequestDto;
import com.ritsard.baisard.global.auth.dto.request.UpdateMemberPasswordDto;
import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

public interface AuthService {
    LoginResponseDto loginMember(LoginRequestDto dto,
                                 HttpServletRequest request,
                                 HttpServletResponse response);

    void registerAdmin(SignUpAdminDto dto) throws CryptoKeyException, EncryptionException;

    void registerSuperAdmin(SignupRequestDto dto) throws CryptoKeyException, EncryptionException;

    void registerCashier(SignupRequestDto dto) throws CryptoKeyException, EncryptionException;

    void resetPassword(UpdateMemberPasswordDto dto);
}
