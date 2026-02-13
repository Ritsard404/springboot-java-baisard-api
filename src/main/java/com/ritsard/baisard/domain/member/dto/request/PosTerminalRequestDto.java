package com.ritsard.baisard.domain.member.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PosTerminalRequestDto(
        UUID uuidPosTerminal,
        String posName,
        String registeredName,
        String address,
        String vatTinNumber,
        Integer vat,
        BigDecimal discountMax,
        String costCenter,
        String branchCenter,
        String useCenter,
        String printerName,
        boolean isRetailType
) {
}
