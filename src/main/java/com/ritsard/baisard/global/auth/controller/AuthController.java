package com.ritsard.baisard.global.auth.controller;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.global.auth.dto.request.SignupRequestDto;
import com.ritsard.baisard.global.auth.service.AuthService;
import com.ritsard.baisard.global.auth.service.AuthServiceImpl;
import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.service.login.LoginService;
import com.ritsard.baisard.jwt.service.sign.SignService;
import com.ritsard.baisard.jwt.utils.permission.PermissionTypeProvider;
import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ApiResponse<?> login(
            @RequestBody LoginRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ApiResponse.ok(
                authService.loginMember(dto, request, response)
        );
    }

    @Operation(summary = "Super Admin Registration (TESTING)")
    @PostMapping("/register/super-admin")
    public ApiResponse<?> registerSuperAdmin(
            @Valid @RequestBody SignupRequestDto dto
    ) throws CryptoKeyException, EncryptionException {
        authService.registerSuperAdmin(dto);
        return ApiResponse.ok();
    }

    @Operation(summary = "Admin Registration")
    @PostMapping("/register/admin")
    public ApiResponse<?> registerAdmin(
            @Valid @RequestBody SignupRequestDto dto
    ) throws CryptoKeyException, EncryptionException {
        authService.registerAdmin(dto);
        return ApiResponse.ok();
    }

    @Operation(summary = "Cashier Registration")
    @PostMapping("/register/cashier")
    public ApiResponse<?> registerCashier(
            @Valid @RequestBody SignupRequestDto dto
    ) throws CryptoKeyException, EncryptionException {
        authService.registerCashier(dto);
        return ApiResponse.ok();
    }

    @Operation(summary = "Approve Member (SUPERADMIN only)")
    @PostMapping("/approve/{memberId}")
    public ApiResponse<?> approveMember(
            @PathVariable UUID memberId
    ) {
        authService.approveMember(memberId);
        return ApiResponse.ok();
    }
}
