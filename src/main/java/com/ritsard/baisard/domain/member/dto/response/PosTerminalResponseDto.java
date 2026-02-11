package com.ritsard.baisard.domain.member.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PosTerminalResponseDto(
        UUID uuidPosTerminal,
        String minNumber,
        String accreditationNumber,
        String ptuNumber,
//        LocalDate dateIssued,
//        LocalDate validUntil,
        String posName,
        String registeredName,
        String operatedBy,
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
