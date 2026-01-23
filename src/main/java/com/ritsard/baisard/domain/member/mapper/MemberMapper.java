package com.ritsard.baisard.domain.member.mapper;

import com.ritsard.baisard.domain.member.dto.response.*;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.repository.projections.MyCashiersProjection;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.model.entity.Permission;
import com.ritsard.baisard.utils.helper.AESConverter;

import java.util.stream.Collectors;

public class MemberMapper {
    public static MemberListDto toMemberListDto(Member member) {
        return MemberListDto.builder()
                .memberId(member.getUuidMember())
                .identifier(member.getIdentifier())
                .approvalStatus(member.getApprovalStatus().name())
                .company(CompanyDto.builder()
                        .uuid(member.getCompany() != null ? member.getCompany().getUuidCompany() : null)
                        .code(member.getCompany() != null ? member.getCompany().getCode() : null)
                        .name(member.getCompany() != null ? member.getCompany().getName() : null)
                        .build())
                .permissions(member.getPermissions().stream()
                        .map(Permission::getPermissionType)
                        .collect(Collectors.toList()))
                .build();
    }

    public static MyCashiersDto toMyCashiersDto(Member p) {
        return MyCashiersDto.builder()
                .cashierId(p.getUuidMember())
                .identifier(p.getIdentifier())
                .name(p.getName())
                .nickName(p.getNickname())
                .isActive(p.isActive())
                .build();
    }

    public static AdminInfoDto toAdminDto(Member member) {
        return AdminInfoDto.builder()
                .identifier(member.getLoginCredentials().stream()
                        .map(LoginCredential::getIdentifier)
                        .findFirst()
                        .orElse(null))
                .name(member.getName())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
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
}
