package com.ritsard.baisard.domain.member.mapper;

import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PosTerminalInfoMapper {  // Helper to map entity to DTO for single lookups
    public PosTerminalResponseDto mapToDto(PosTerminalInfo info) {
        return new PosTerminalResponseDto(
                info.getUuidPosTerminal(),
                info.getMinNumber(),
                info.getAccreditationNumber(),
                info.getPtuNumber(),
                info.getPosName(),
                info.getRegisteredName(),
                info.getOperatedBy(),
                info.getAddress(),
                info.getVatTinNumber(),
                info.getVat(),
                info.getDiscountMax(),
                info.getCostCenter(),
                info.getBranchCenter(),
                info.getUseCenter(),
                info.getPrinterName(),
                info.isRetailType()
        );
    }
}
