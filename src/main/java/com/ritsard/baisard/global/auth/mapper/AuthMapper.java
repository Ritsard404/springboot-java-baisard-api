package com.ritsard.baisard.global.auth.mapper;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthMapper {
    public Member toEntity(SignupRequestDto signupRequestDto) {
        return Member.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())

                .build();
    }
}
