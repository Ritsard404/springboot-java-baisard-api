package com.ritsard.baisard.domain.inventory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VatType {
    VATABLE("Vatable"),
    EXEMPT("Exempt"),
    ZERO("Zero");


    private final String description;
}
