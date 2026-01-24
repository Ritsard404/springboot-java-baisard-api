package com.ritsard.baisard.domain.member.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MyCashiersDto {
    private UUID cashierId;
    private String identifier;
    private String name;
    private String nickName;
    private Boolean isActive;

    @QueryProjection
    public MyCashiersDto(UUID cashierId, String identifier, String name, String nickName, Boolean isActive) {
        this.cashierId = cashierId;
        this.identifier = identifier;
        this.name = name;
        this.nickName = nickName;
        this.isActive = isActive;
    }
}
