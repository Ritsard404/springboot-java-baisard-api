package com.ritsard.baisard.domain.order.controller;

import com.ritsard.baisard.domain.order.dto.request.OrderDto;
import com.ritsard.baisard.domain.order.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/pay")
    public ResponseEntity<Void> payOrder(
            @Valid @RequestBody OrderDto orderDto
    ) {
        orderService.payOrder(orderDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(
            @Valid @RequestBody OrderDto orderDto,
            @RequestParam String managerIdentifier,
            @RequestParam String reason
    ) {
        orderService.cancelOrder(orderDto, managerIdentifier, reason);
        return ResponseEntity.ok().build();
    }
}
