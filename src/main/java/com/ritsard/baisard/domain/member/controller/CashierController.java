package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.request.CashDrawerRequest;
import com.ritsard.baisard.domain.member.dto.request.CashWithdrawRequest;
import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import com.ritsard.baisard.domain.member.service.CashierService;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/cashier")
@RequiredArgsConstructor
@Tag(name = "Cashier (CASHIER & ADMIN)", description = "Cashier drawer management APIs")
public class CashierController {

    private final CashierService cashierService;

    @PostMapping("/cash-in-drawer")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @Operation(
            summary = "Set cash in drawer",
            description = "Sets the initial cash amount in the cashier's drawer at the start of their shift"
    )
    public ApiResponse<?> cashInDrawer(
            @Valid @RequestBody CashDrawerRequest request
    ) {
        log.info("Cash in drawer request: {}", request.getAmount());

        cashierService.cashInDrawer(request.getAmount());

        return ApiResponse.ok(null, "Cash in drawer set successfully");
    }

    @GetMapping("/is-cashed-drawer")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @Operation(
            summary = "Check if drawer has cash",
            description = "Checks if the cashier's drawer currently has cash set"
    )
    public ApiResponse<Boolean> isCashedDrawer() {

        Boolean isCashed = cashierService.isCashedDrawer();

        return ApiResponse.ok(
                isCashed,
                isCashed ? "Drawer has cash" : "Drawer is empty"
        );
    }

    @PostMapping("/cash-out-drawer")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @Operation(
            summary = "Cash out drawer",
            description = "Records the cash out amount at the end of cashier's shift, requires manager approval"
    )
    public ApiResponse<?> cashOutDrawer(
            @Valid @RequestBody CashWithdrawRequest request
    ) {
        log.info(
                "Cash out drawer request: amount={}, managerIdentifier={}",
                request.getAmount(),
                request.getManagerIdentifier()
        );

        cashierService.cashOutDrawer(
                request.getAmount(),
                request.getManagerIdentifier()
        );

        return ApiResponse.ok(null, "Cash out drawer completed successfully");
    }

    @PostMapping("/cash-withdraw-drawer")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN')")
    @Operation(
            summary = "Withdraw cash from drawer",
            description = "Withdraws cash from the drawer during shift, requires manager approval"
    )
    public ApiResponse<?> cashWithdrawDrawer(
            @Valid @RequestBody CashWithdrawRequest request
    ) {
        log.info(
                "Cash withdraw drawer request: amount={}, managerIdentifier={}",
                request.getAmount(),
                request.getManagerIdentifier()
        );

        cashierService.cashWithdrawDrawer(
                request.getAmount(),
                request.getManagerIdentifier()
        );

        return ApiResponse.ok(null, "Cash withdrawn from drawer successfully");
    }
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
                cashierService.cashierInfo(cashierId),
                "Cashier info fetched successfully"
        );
    }

    @PutMapping("/cashiers")
    @Operation(summary = "Update cashier info")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<?> updateCashierInfo(
            @Valid @RequestBody CashierInfoDto dto
    ) {
        cashierService.updateCashierInfo(dto);
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
                cashierService.myCashiers(keyword, page, size, sortBy, direction),
                "Cashiers fetched successfully"
        );
    }
}
