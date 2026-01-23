package com.ritsard.baisard.global.auth.dto.request;

import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAdminDto extends SignupRequestDto {
    @NotNull
    private String companyName;
    private String companyCode;
}
