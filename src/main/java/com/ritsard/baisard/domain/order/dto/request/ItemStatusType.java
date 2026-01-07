package com.ritsard.baisard.domain.order.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatusType {
    VOID("Void"),
    PAID("Paid");

    private final String description;
}
