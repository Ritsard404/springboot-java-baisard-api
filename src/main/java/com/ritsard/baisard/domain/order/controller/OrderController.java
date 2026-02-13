package com.ritsard.baisard.domain.order.controller;

import com.ritsard.baisard.domain.order.dto.request.OrderDto;
import com.ritsard.baisard.domain.order.service.IOrderService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Order Management API", description = "Manage pay and cancel transactions")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/pay")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CASHIER')")
    public ApiResponse<?> payOrder(
            @Valid @RequestBody OrderDto orderDto
    ) {
        orderService.payOrder(orderDto);
        return ApiResponse.ok("Order paid successfully.");
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CASHIER')")
    public ApiResponse<?> cancelOrder(
            @Valid @RequestBody OrderDto orderDto,
            @RequestParam String managerIdentifier,
            @RequestParam String reason
    ) {
        orderService.cancelOrder(orderDto, managerIdentifier, reason);
        return ApiResponse.ok("Order cancelled successfully.");
    }
}
