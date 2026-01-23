package com.ritsard.baisard.domain.member.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class AdminInfoDto {
    private String identifier;
    private String name;
    private String nickName;
    private String email;
    private String phoneNumber;
    private LocalDate birthdate;
}
