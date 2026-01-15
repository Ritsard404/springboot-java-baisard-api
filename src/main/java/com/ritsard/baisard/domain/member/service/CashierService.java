package com.ritsard.baisard.domain.member.service;

import java.math.BigDecimal;

public interface CashierService {

    void cashInDrawer(BigDecimal amount);

    Boolean isCashedDrawer();

    void cashOutDrawer(BigDecimal amount, String managerIdentifier);

    void cashWithdrawDrawer(BigDecimal amount, String managerIdentifier);
}
