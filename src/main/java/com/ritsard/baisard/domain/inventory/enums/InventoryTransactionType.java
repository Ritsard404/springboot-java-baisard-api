package com.ritsard.baisard.domain.inventory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryTransactionType {
    IN("In"),
    ADJUSTMENT("Adjustment"),
    OUT("Out");

    private final String description;
}
