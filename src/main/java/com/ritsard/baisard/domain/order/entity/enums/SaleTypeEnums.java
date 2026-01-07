package com.ritsard.baisard.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SaleTypeEnums {
    CARD("Card"),
    EPAYMENT("E-Payment");

    private final String description;
}
