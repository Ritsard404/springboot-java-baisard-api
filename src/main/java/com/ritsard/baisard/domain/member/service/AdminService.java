package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.request.UpdateCompanyDto;
import com.ritsard.baisard.domain.member.dto.response.AdminInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {

    AdminInfoDto adminProfile();

    void updateAdminProfile(AdminInfoDto adminInfoDto);

    Page<MyCashiersDto> myCashiers(String keyword,
                                   Integer page,
                                   Integer size,
                                   String sortBy,
                                   String direction);

    CashierInfoDto cashierInfo(UUID cashierId);

    void updateCashierInfo(CashierInfoDto dto);

    CompanyDto companyInfo();

    void updateCompany(UpdateCompanyDto dto);
}
