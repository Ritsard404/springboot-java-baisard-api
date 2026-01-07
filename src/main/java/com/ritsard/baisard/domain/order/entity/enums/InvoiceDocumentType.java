package com.ritsard.baisard.domain.order.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceDocumentType {
    INVOICE("Invoice"),
    ZREPORT("Z-Report"),
    XREPORT("X-Report");

    private final String description;
}
