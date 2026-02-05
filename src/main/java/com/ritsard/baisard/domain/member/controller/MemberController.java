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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "APIs for managing members")
@PreAuthorize("hasAnyAuthority('SUPERADMIN')")
public class MemberController {

    private final MemberService memberService;

    /* =========================================================
       MEMBER LIST (SUPERADMIN)
       ========================================================= */

    @GetMapping
    @Operation(summary = "Get members")
    public ApiResponse<?> getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemberApprovalStatus approvalStatus,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identifier") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Page<MemberListDto> members = memberService.getMembers(
                keyword,
                approvalStatus,
                page,
                size,
                sortBy,
                direction
        );

        return ApiResponse.ok(members, "Members fetched successfully");
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "Get member information")
    public ApiResponse<MemberInfoDto> getMember(
            @PathVariable UUID memberId
    ) {
        MemberInfoDto memberInfo = memberService.getMember(memberId);
        return ApiResponse.ok(memberInfo);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "Update member and company information")
    public ApiResponse<?> updateMember(
            @PathVariable UUID memberId,
            @Valid @RequestBody MemberInfoDto memberInfoDto
    ) {
        memberService.updateMemberInfo(memberInfoDto, memberId);
        return ApiResponse.ok("Member information updated successfully");
    }


    /* =========================================================
       ACCOUNT STATE (SUPERADMIN)
       ========================================================= */

    @PostMapping("/approve/{memberId}")
    @Operation(summary = "Approve member")
    public ApiResponse<?> approveMember(
            @PathVariable UUID memberId
    ) {
        memberService.approveMember(memberId);
        return ApiResponse.ok("Member approved successfully");
    }

    @PostMapping("/activate/{memberId}")
    @Operation(summary = "Activate member")
    public ApiResponse<?> activateMember(
            @PathVariable UUID memberId
    ) {
        memberService.activateMember(memberId);
        return ApiResponse.ok("Member activated successfully");
    }

    @PostMapping("/deactivate/{memberId}")
    @Operation(summary = "Deactivate member")
    public ApiResponse<?> deactivateMember(
            @PathVariable UUID memberId
    ) {
        memberService.deActivateMember(memberId);
        return ApiResponse.ok("Member deactivated successfully");
    }
}
