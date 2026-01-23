package com.ritsard.baisard.global.auth.dto.request;

import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SignUpAdminDto extends SignupRequestDto {
    @NotNull
    private String companyName;
    private String companyCode;
}
