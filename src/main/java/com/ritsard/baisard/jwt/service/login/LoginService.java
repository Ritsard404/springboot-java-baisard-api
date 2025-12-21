package com.ritsard.baisard.jwt.service.login;

import com.ritsard.baisard.jwt.dto.login.LoginRequestDto;
import com.ritsard.baisard.jwt.dto.login.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginService {
    LoginResponseDto login(LoginRequestDto dto, HttpServletRequest request, HttpServletResponse response);
}
