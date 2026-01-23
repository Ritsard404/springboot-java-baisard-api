package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

public interface CashierService {

    void cashInDrawer(BigDecimal amount);

    Boolean isCashedDrawer();

    void cashOutDrawer(BigDecimal amount, String managerIdentifier);

    void cashWithdrawDrawer(BigDecimal amount, String managerIdentifier);

    Page<MyCashiersDto> myCashiers(String keyword,
                                   Integer page,
                                   Integer size,
                                   String sortBy,
                                   String direction);

    CashierInfoDto cashierInfo(UUID cashierId);

    void updateCashierInfo(CashierInfoDto dto);
}
