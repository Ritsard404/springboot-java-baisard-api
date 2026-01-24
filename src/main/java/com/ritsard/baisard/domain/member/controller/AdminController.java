package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.request.UpdateCompanyDto;
import com.ritsard.baisard.domain.member.dto.response.AdminInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import com.ritsard.baisard.domain.member.service.AdminService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin (ADMIN)", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    /* =========================================================
       CASHIER MANAGEMENT (ADMIN)
       ========================================================= */

    @GetMapping("/cashiers/{cashierId}")
    @Operation(summary = "Get cashier info")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<CashierInfoDto> cashierInfo(
            @PathVariable UUID cashierId
    ) {
        return ApiResponse.ok(
                adminService.cashierInfo(cashierId),
                "Cashier info fetched successfully"
        );
    }

    @PutMapping("/cashiers")
    @Operation(summary = "Update cashier info")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<?> updateCashierInfo(
            @Valid @RequestBody CashierInfoDto dto
    ) {
        adminService.updateCashierInfo(dto);
        return ApiResponse.ok("Cashier info updated successfully");
    }

    @GetMapping("/my-cashiers")
    @Operation(summary = "Get my cashiers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Page<MyCashiersDto>> myCashiers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "identifier") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.ok(
                adminService.myCashiers(keyword, page, size, sortBy, direction),
                "Cashiers fetched successfully"
        );
    }

    /* =========================================================
       ADMIN PROFILE
       ========================================================= */

    @GetMapping("/profile/{adminId}")
    @Operation(summary = "Get admin profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ApiResponse<AdminInfoDto> adminProfile(
            @PathVariable UUID adminId
    ) {
        return ApiResponse.ok(
                adminService.adminProfile(adminId),
                "Admin profile fetched successfully"
        );
    }


    @PutMapping("/profile")
    @Operation(summary = "Update admin profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ApiResponse<?> updateAdminProfile(
            @Valid @RequestBody AdminInfoDto dto
    ) {
        adminService.updateAdminProfile(dto);
        return ApiResponse.ok("Admin profile updated successfully");
    }

    /* =========================================================
       COMPANY MANAGEMENT (ADMIN)
       ========================================================= */

    @GetMapping("/company")
    @Operation(summary = "Get company info")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ApiResponse<CompanyDto> companyInfo() {
        return ApiResponse.ok(
                adminService.companyInfo(),
                "Company info fetched successfully"
        );
    }

    @PutMapping("/company")
    @Operation(summary = "Update company info")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ApiResponse<?> updateCompany(
            @Valid @RequestBody UpdateCompanyDto dto
    ) {
        adminService.updateCompany(dto);
        return ApiResponse.ok("Company info updated successfully");
    }
}
