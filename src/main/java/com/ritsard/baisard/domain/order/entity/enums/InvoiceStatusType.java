package com.ritsard.baisard.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatusType {
    CANCELLED("Cancelled"),
    RETURNED("Returned"),
    VOID("Void"),
    PENDING("Pending"),
    PAID("Paid");

    private final String description;
}
