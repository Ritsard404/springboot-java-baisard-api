package com.ritsard.baisard.jwt.dto.login;

import com.ritsard.baisard.jwt.dto.TokenDto;
import lombok.*;

import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto extends TokenDto {
    private UUID uuidMember;
    private String identifier;
    private String name;
    private Set<String> permissions;
}
