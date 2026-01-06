package com.ritsard.baisard.domain.inventory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {
    RESALE("Resale"),
    WHOLESALE("Whole Sale");

    private final String description;
}
