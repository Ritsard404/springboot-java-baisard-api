package com.ritsard.baisard.jwt.dto.login;

import com.ritsard.baisard.jwt.model.enums.LoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    private @NotBlank String identifier;
    private @NotBlank String password;
    private @NotNull LoginType loginType;
}
