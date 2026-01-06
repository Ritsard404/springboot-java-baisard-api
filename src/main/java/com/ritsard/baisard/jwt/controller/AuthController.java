package com.ritsard.baisard.jwt.controller;

import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.model.entity.BaseMember;
import com.ritsard.baisard.jwt.service.login.LoginService;
import com.ritsard.baisard.jwt.service.sign.SignService;
import com.ritsard.baisard.jwt.utils.permission.IPermissionType;
import com.ritsard.baisard.jwt.utils.permission.PermissionTypeProvider;
import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.enums.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
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
            summary = "login"
    )
    public ApiResponse<?> login(@RequestBody LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDto object = this.loginService.login(dto, request, response);
        return ApiResponse.ok(object);
    }

    @Operation(
            summary = "Check for duplicate IDs",
            description = "When registering as a member, we check for duplicate IDs."
    )
    @GetMapping({"/checkIdentifier"})
    public ApiResponse<?> checkIdentifier(@RequestParam String identifier) {
        this.signService.checkIdentifier(identifier);
        return ApiResponse.success(SuccessCode.SELECT_SUCCESS);
    }

    @Operation(
            summary = "Check for duplicate emails",
            description = "When registering as a member, we check for duplicate email addresses."
    )
    @GetMapping({"/check-email"})
    public ApiResponse<?> checkEmail(@RequestParam String email) {
        this.signService.checkEmail(email);
        return ApiResponse.success(SuccessCode.SELECT_SUCCESS);
    }

    @Operation(
            summary = "log out",
            description = "Delete the refresh token cookie."
    )
    @PostMapping({"/logout"})
    public ApiResponse<?> logout(HttpServletResponse response) {
        this.signService.logout(response);
        return ApiResponse.ok();
    }

    @Operation(
            summary = "Cancel membership",
            description = "Soft delete members by UUID."
    )

    @DeleteMapping({"/withdraw/{uuidMember}"})
    public ApiResponse<?> withdraw(@PathVariable UUID uuidMember, HttpServletResponse response) {
        this.signService.withdraw(uuidMember, response);
        return ApiResponse.success(SuccessCode.DELETE_SUCCESS);
    }

    protected Set<String> getPermissionsByRole(String role) {
        return (Set) Arrays.stream(this.permissionTypeProvider.getPermissionTypes()).filter((p) -> p.name().equalsIgnoreCase(role)).map(IPermissionType::name).collect(Collectors.toSet());
    }

    protected abstract T createNewBaseMember(SignupRequestDto dto);
}
