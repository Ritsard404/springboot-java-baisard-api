package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.request.UpdateCompanyDto;
import com.ritsard.baisard.domain.member.dto.response.AdminInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.utils.exceptions.crypto.CryptoKeyException;
import com.ritsard.baisard.utils.exceptions.crypto.EncryptionException;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {

    AdminInfoDto adminProfile(UUID adminId);

    void updateAdminProfile(AdminInfoDto adminInfoDto);

    Page<MyCashiersDto> myCashiers(String keyword,
                                   Integer page,
                                   Integer size,
                                   String sortBy,
                                   String direction);
    void registerCashier(SignupRequestDto dto) throws CryptoKeyException, EncryptionException;

    CashierInfoDto cashierInfo(UUID cashierId);

    void updateCashierInfo(CashierInfoDto dto);

    CompanyDto companyInfo();

    void updateCompany(UpdateCompanyDto dto);
}
