package com.ritsard.baisard.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateCompanyDto {
    @NotNull
    private UUID uuid;
    @NotNull
    private String code;
    @NotNull
    private String name;
    private String encryptedCompanyImageId;
}