package com.ritsard.baisard.domain.member.mapper;

import com.ritsard.baisard.domain.member.dto.response.*;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.global.utils.AESUtil;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.utils.helper.AESConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberMapper {

    public static AdminInfoDto toAdminDto(Member member, AESUtil aesUtil) {
        return AdminInfoDto.builder()
                .identifier(member.getLoginCredentials().stream()
                        .map(LoginCredential::getIdentifier)
                        .findFirst()
                        .orElse(null))
                .name(member.getName())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .phoneNumber(aesUtil.decrypt(member.getPhoneNumber()))
                .birthdate(member.getBirthdate())
                .build();
    }

    public static CashierInfoDto toCashierDto(Member member) {
        return CashierInfoDto.builder()
                .cashierId(member.getUuidMember())
                .identifier(member.getLoginCredentials().stream()
                        .map(LoginCredential::getIdentifier)
                        .findFirst()
                        .orElse(null))
                .name(member.getName())
                .nickName(member.getNickname())
                .isActive(member.isActive())
                .build();
    }

    public static void updateCashierFromDto(CashierInfoDto dto, Member member) {
        if (dto == null || member == null) return;

        // Update basic info
        member.setName(dto.getName());
        member.setNickname(dto.getNickName());
        if (dto.getIsActive()) {

            member.restoreMember();
        } else {
            member.softDelete();
        }
        if (dto.getIdentifier() != null && !member.getLoginCredentials().isEmpty()) {
            member.getLoginCredentials().get(0).setIdentifier(dto.getIdentifier());
        }
    }

    public static void updateAdminFromDto(AdminInfoDto dto, Member member, AESConverter aesConverter) {
        if (dto == null || member == null) return;

        // 1. Update Basic Profile Info
        member.setName(dto.getName());
        member.setNickname(dto.getNickName());
        member.setEmail(dto.getEmail());
        member.setBirthdate(dto.getBirthdate());

        // 2. Handle Encrypted Phone Number
        if (dto.getPhoneNumber() != null) {
            try {
                // Remove dashes/spaces and encrypt before setting
                String sanitizedPhone = dto.getPhoneNumber().replaceAll("[^0-9]", "");
                String encryptedPhone = aesConverter.encryption(sanitizedPhone);
                member.setPhoneNumber(encryptedPhone);
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt phone number during update", e);
            }
        }

        // 3. Update Login Identifier (Username)
        if (dto.getIdentifier() != null && !member.getLoginCredentials().isEmpty()) {
            // Updates the primary login identifier
            member.getLoginCredentials().get(0).setIdentifier(dto.getIdentifier());
        }
    }

    public static MemberInfoDto toMemberInfoDto(Member member, AESUtil aesUtil, CompanyMapper companyMapper) {
        return MemberInfoDto.builder()
                .identifier(member.getIdentifier())
                .name(member.getName())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .birthdate(member.getBirthdate())
                .approvalStatus(member.getApprovalStatus())
                .isActive(member.isActive())
                .classification(member.getClassification())
                .companyDto(member.getCompany() != null ? companyMapper.toCompanyDto(member.getCompany()) : null)
                .build();
    }

    public static void updateMemberEntity(Member member, MemberInfoDto dto, AESUtil aesUtil) {
        // 1. Update Base Fields
        member.setName(dto.getName());
        member.setNickname(dto.getNickName());
        member.setEmail(dto.getEmail());
        member.setBirthdate(dto.getBirthdate());
        member.setPhoneNumber(dto.getPhoneNumber());
//        if (dto.getPhoneNumber() != null) {
//            member.setPhoneNumber(aesUtil.encrypt(dto.getPhoneNumber()));
//        }

        // 2. Update Login Identifier (First credential)
//        member.getLoginCredentials().stream()
//                .findFirst()
//                .ifPresent(cred -> cred.setIdentifier(dto.getIdentifier()));

        // 3. Update Company Details (Delegates to CompanyMapper)
        if (dto.getCompanyDto() != null && member.getCompany() != null) {
            // We reuse the logic you already have for companies
            updateCompanyDetails(member.getCompany(), dto.getCompanyDto());
        }
    }

    // Internal helper for the nested company update
    private static void updateCompanyDetails(Company company, CompanyDto dto) {
        company.setName(dto.getName());
        company.setCode(dto.getCode());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
    }
}
