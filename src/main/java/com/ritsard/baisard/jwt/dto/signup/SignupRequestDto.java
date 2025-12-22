package com.ritsard.baisard.jwt.dto.signup;

import com.ritsard.baisard.jwt.model.enums.LoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private @NotBlank String identifier;
    private @NotBlank String password;
    private @NotNull LoginType loginType;
    private LocalDate birthDate;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
}
