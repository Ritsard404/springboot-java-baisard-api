package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.request.CashDrawerRequest;
import com.ritsard.baisard.domain.member.dto.request.CashWithdrawRequest;
import com.ritsard.baisard.domain.member.service.CashierService;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cashier")
@RequiredArgsConstructor
@Tag(name = "Cashier", description = "Cashier drawer management APIs")
public class CashierController {

    private final CashierService cashierService;

    @PostMapping("/cash-in-drawer")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN', 'SUPERADMIN')")
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
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN', 'SUPERADMIN')")
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
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN', 'SUPERADMIN')")
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
    @PreAuthorize("hasAnyAuthority('CASHIER', 'ADMIN', 'SUPERADMIN')")
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
}
