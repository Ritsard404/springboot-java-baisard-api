package com.ritsard.baisard.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {
    PWD("PWD"),
    SENIOR("Senior Citizen"),
    OTHERS("Others");

    private final String description;
}
