package com.ritsard.baisard.domain.member.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MyCashiersDto {
    @NotNull
    private UUID cashierId;
    private String identifier;
    private String name;
    private String nickName;
    private Boolean isActive;
}
