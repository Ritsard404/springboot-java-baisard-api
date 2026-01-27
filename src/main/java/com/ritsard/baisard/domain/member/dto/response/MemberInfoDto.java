package com.ritsard.baisard.domain.member.dto.response;

import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.enums.PermissionType;
import com.ritsard.baisard.jwt.model.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor  // Added for Jackson
@AllArgsConstructor
public class MemberInfoDto {
    @NotBlank
    private String identifier;
    @NotBlank
    private String name;
    @NotBlank
    private String nickName;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotNull
    private LocalDate birthdate;
    private MemberApprovalStatus approvalStatus;
    private Boolean isActive;
    private PermissionType classification;

    @NotNull
    private CompanyDto companyDto;
}
