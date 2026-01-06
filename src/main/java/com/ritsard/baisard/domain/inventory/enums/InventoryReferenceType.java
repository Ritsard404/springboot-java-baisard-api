package com.ritsard.baisard.domain.inventory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryReferenceType {
    INVOICE("Invoice"),
    VOID("Void"),
    RETURN("Return");

    private final String description;
}
