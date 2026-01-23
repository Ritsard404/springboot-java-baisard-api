package com.ritsard.baisard.domain.member.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MemberCompanyInfoDto {
    @NotBlank
    private UUID companyId;
    @NotBlank
    private String name;
    private String logoImageUrl;
    private String code;
    private String email;
    private String phone;
}
