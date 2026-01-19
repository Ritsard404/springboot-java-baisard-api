package com.ritsard.baisard.global.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto extends com.ritsard.baisard.jwt.dto.signup.SignupRequestDto {
    @NotNull
    private String companyName;
    @NotNull
    private String companyCode;
}
