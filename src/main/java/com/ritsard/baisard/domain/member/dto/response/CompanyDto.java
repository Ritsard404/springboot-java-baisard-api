package com.ritsard.baisard.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private UUID uuid;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String logoImageUrl;
}