package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.response.*;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.service.MemberService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "APIs for managing members")
public class MemberController {

    private final MemberService memberService;

    /* =========================================================
       MEMBER LIST (SUPERADMIN)
       ========================================================= */

    @GetMapping
    @Operation(summary = "Get members")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<Page<MemberListDto>> getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemberApprovalStatus approvalStatus,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identifier") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<MemberListDto> members = memberService.getMembers(
                keyword,
                approvalStatus,
                companyId,
                page,
                size,
                sortBy,
                direction
        );

        return ApiResponse.ok(members, "Members fetched successfully");
    }

    /* =========================================================
       ADMIN PROFILE
       ========================================================= */

    @GetMapping("/admin/profile")
    @Operation(summary = "Get admin profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ApiResponse<AdminInfoDto> adminProfile() {
        return ApiResponse.ok(
                memberService.adminProfile(),
                "Admin profile fetched successfully"
        );
    }

    @PutMapping("/admin/profile")
    @Operation(summary = "Update admin profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ApiResponse<?> updateAdminProfile(
            @Valid @RequestBody AdminInfoDto dto
    ) {
        memberService.updateAdminProfile(dto);
        return ApiResponse.ok("Admin profile updated successfully");
    }


    /* =========================================================
       ACCOUNT STATE (SUPERADMIN)
       ========================================================= */

    @PostMapping("/approve/{memberId}")
    @Operation(summary = "Approve member")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<?> approveMember(
            @PathVariable UUID memberId
    ) {
        memberService.approveMember(memberId);
        return ApiResponse.ok("Member approved successfully");
    }

    @PostMapping("/activate/{memberId}")
    @Operation(summary = "Activate member")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<?> activateMember(
            @PathVariable UUID memberId
    ) {
        memberService.activateMember(memberId);
        return ApiResponse.ok("Member activated successfully");
    }

    @PostMapping("/deactivate/{memberId}")
    @Operation(summary = "Deactivate member")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<?> deactivateMember(
            @PathVariable UUID memberId
    ) {
        memberService.deActivateMember(memberId);
        return ApiResponse.ok("Member deactivated successfully");
    }
}
