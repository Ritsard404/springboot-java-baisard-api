package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.response.MemberListDto;
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
//    @PreAuthorize("hasAuthority('SUPERADMIN')")
@Tag(name = "Members", description = "APIs for managing members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(
            summary = "Get members",
            description = "Retrieve members with optional keyword search, approval status, company filter, pagination, and sorting. Keyword searches identifier first, then company name."
    )
    public ApiResponse<Page<MemberListDto>> getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemberApprovalStatus approvalStatus,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identifier") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Fetching members with keyword: {}, approvalStatus: {}, companyId: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                keyword, approvalStatus, companyId, page, size, sortBy, direction);

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
}
