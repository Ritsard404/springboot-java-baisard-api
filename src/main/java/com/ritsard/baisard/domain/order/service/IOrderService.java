package com.ritsard.baisard.domain.order.service;

import com.ritsard.baisard.domain.order.dto.request.OrderDto;

import java.util.UUID;

public interface IOrderService {
    void payOrder(OrderDto orderDto);
    void cancelOrder(OrderDto orderDto, String managerIdentifier, String reason);
}
