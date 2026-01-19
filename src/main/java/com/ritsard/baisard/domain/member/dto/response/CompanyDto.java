package com.ritsard.baisard.domain.member.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CompanyDto {
    private UUID uuid;
    private String code;
    private String name;
}