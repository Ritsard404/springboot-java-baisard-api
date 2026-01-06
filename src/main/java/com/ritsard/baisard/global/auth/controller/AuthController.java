package com.ritsard.baisard.global.auth.controller;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.global.auth.service.AuthService;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.service.login.LoginService;
import com.ritsard.baisard.jwt.service.sign.SignService;
import com.ritsard.baisard.jwt.utils.permission.PermissionTypeProvider;
import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/auth")
public class AuthController extends com.ritsard.baisard.jwt.controller.AuthController<Member> {
    protected final AuthService authService;

    public AuthController(LoginService loginService, SignService signService, PermissionTypeProvider permissionTypeProvider, AuthService authService) {
        super(loginService, signService, permissionTypeProvider);
        this.authService = authService;
    }

    @Operation(summary = "Super Admin Registration (TESTING)")
    @PostMapping("/register/super-admin")
    public ApiResponse<?> registerSuperAdmin(@Valid @RequestBody SignupRequestDto dto) throws CryptoKeyException, EncryptionException {
        authService.registerSuperAdmin(dto);
        return ApiResponse.ok();
    }

    @Operation(summary = "Admin Registration")
    @PostMapping("/register/admin")
    public ApiResponse<?> registerAdmin(@Valid @RequestBody SignupRequestDto dto) throws CryptoKeyException, EncryptionException {
        authService.registerAdmin(dto);
        return ApiResponse.ok();
    }

    @Operation(summary = "Cashier Registration")
    @PostMapping("/register/cashier")
    public ApiResponse<?> registerCashier(@Valid @RequestBody SignupRequestDto dto) throws CryptoKeyException, EncryptionException {
        authService.registerCashier(dto);
        return ApiResponse.ok();
    }

    @Override
    protected Member createNewBaseMember(SignupRequestDto dto) {
        return null;
    }
}
