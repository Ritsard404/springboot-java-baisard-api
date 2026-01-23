package com.ritsard.baisard.domain.member.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor // Required for Jackson/Frameworks
@AllArgsConstructor
@Builder
public class CashierInfoDto {
    @NotNull
    private UUID cashierId;
    private String identifier;
    private String name;
    private String nickName;
    private Boolean isActive;
}
