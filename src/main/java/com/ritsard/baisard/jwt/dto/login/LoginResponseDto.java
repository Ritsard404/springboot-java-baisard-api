package com.ritsard.baisard.jwt.dto.login;

import com.ritsard.baisard.jwt.dto.TokenDto;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto extends TokenDto {
    private UUID uuidMember;
    private String identifier;
    private String name;
    private Set<String> permissions;
}
