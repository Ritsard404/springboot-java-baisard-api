package com.ritsard.baisard.domain.member.mapper;

import com.ritsard.baisard.domain.member.dto.request.PosTerminalRequestDto;
import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
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

    public void adminPosInfoFromDto(PosTerminalRequestDto dto, PosTerminalInfo info) {
        if (dto == null) return;

        info.setPosName(dto.posName());
        info.setRegisteredName(dto.registeredName());
        info.setAddress(dto.address());
        info.setVatTinNumber(dto.vatTinNumber());
        info.setVat(dto.vat());
        info.setDiscountMax(dto.discountMax());
        info.setCostCenter(dto.costCenter());
        info.setBranchCenter(dto.branchCenter());
        info.setUseCenter(dto.useCenter());
        info.setPrinterName(dto.printerName());
        info.setRetailType(dto.isRetailType());
    }
}
