package com.ritsard.baisard.jwt.controller;

import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.service.login.LoginService;
import com.ritsard.baisard.jwt.service.sign.SignService;
import com.ritsard.baisard.jwt.utils.permission.PermissionType;
import com.ritsard.baisard.jwt.utils.permission.PermissionTypeProvider;
import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.enums.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public abstract class AuthController<T extends BaseMember> {
    private final LoginService loginService;
    protected final SignService signService;
    protected final PermissionTypeProvider permissionTypeProvider;

    @PostMapping({"/login"})
    @Operation(
            summary = "로그인"
    )
    public ApiResponse<?> login(@RequestBody LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDto object = this.loginService.login(dto, request, response);
        return ApiResponse.ok(object);
    }

    @Operation(
            summary = "아이디 중복 확인",
            description = "회원가입 시 아이디 중복 여부를 확인합니다."
    )
    @GetMapping({"/checkIdentifier"})
    public ApiResponse<?> checkIdentifier(@RequestParam String identifier) {
        this.signService.checkIdentifier(identifier);
        return ApiResponse.success(SuccessCode.SELECT_SUCCESS);
    }

    @Operation(
            summary = "이메일 중복 확인",
            description = "회원가입 시 이메일 중복 여부를 확인합니다."
    )
    @GetMapping({"/check-email"})
    public ApiResponse<?> checkEmail(@RequestParam String email) {
        this.signService.checkEmail(email);
        return ApiResponse.success(SuccessCode.SELECT_SUCCESS);
    }

    @Operation(
            summary = "로그아웃",
            description = "리프레시 토큰 쿠키를 삭제합니다."
    )
    @PostMapping({"/logout"})
    public ApiResponse<?> logout(HttpServletResponse response) {
        this.signService.logout(response);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "UUID로 회원을 soft delete 합니다."
    )
    @DeleteMapping({"/withdraw/{uuidMember}"})
    public ApiResponse<?> withdraw(@PathVariable UUID uuidMember, HttpServletResponse response) {
        this.signService.withdraw(uuidMember, response);
        return ApiResponse.success(SuccessCode.DELETE_SUCCESS);
    }

    protected Set<String> getPermissionsByRole(String role) {
        return (Set) Arrays.stream(this.permissionTypeProvider.getPermissionTypes()).filter((p) -> p.name().equalsIgnoreCase(role)).map(PermissionType::name).collect(Collectors.toSet());
    }

    protected abstract T createNewBaseMember(SignupRequestDto dto);
}
