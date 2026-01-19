package com.ritsard.baisard.auth;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.enums.PermissionType;
import com.ritsard.baisard.global.auth.service.AuthServiceImpl;
import com.ritsard.baisard.jwt.dto.signup.SignupRequestDto;
import com.ritsard.baisard.jwt.model.entity.Permission;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.jwt.repository.login.LoginCredentialRepository;
import com.ritsard.baisard.jwt.repository.login.PermissionRepository;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.helper.AESConverter;
import com.ritsard.baisard.utils.log.LoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private BaseMemberRepository<Member> baseMemberRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private LoginCredentialRepository loginCredentialRepository;
    @Mock
    private AESConverter aesConverter;
    @Mock
    private LoggingService loggingService;
    @Mock
    private MemberRedisService memberRedisService;
    @Mock
    private AuthManager<Member> authManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        signupRequestDto = new SignupRequestDto();
        signupRequestDto.setIdentifier("user123");
        signupRequestDto.setEmail("user@example.com");
        signupRequestDto.setName("User Test");
        signupRequestDto.setNickname("usertest");
        signupRequestDto.setPhoneNumber("09171234567");
        signupRequestDto.setPassword("password123");

//        // Stub PermissionRepository to return a Permission for each type
//        Mockito.when(permissionRepository.findByPermissionType("ADMIN"))
//                .thenReturn(java.util.Optional.of(Permission.builder()
//                        .permissionType("ADMIN")
//                        .build()));
//        Mockito.when(permissionRepository.findByPermissionType("SUPERADMIN"))
//                .thenReturn(java.util.Optional.of(Permission.builder()
//                        .permissionType("SUPERADMIN")
//                        .build()));
//        Mockito.when(permissionRepository.findByPermissionType("CASHIER"))
//                .thenReturn(java.util.Optional.of(Permission.builder()
//                        .permissionType("CASHIER")
//                        .build()));
    }
//
//    @Test
//    void registerAdmin_shouldCallSignupWithAdminPermission() throws Exception {
//
//        Mockito.when(permissionRepository.findByPermissionType("ADMIN"))
//                .thenReturn(java.util.Optional.of(Permission.builder()
//                        .permissionType("ADMIN")
//                        .build()));
//        AuthServiceImpl spy = Mockito.spy(authService);
//
//        spy.registerAdmin(signupRequestDto);
//
//        verify(spy, times(1)).signup(
//                eq(signupRequestDto),
//                any(Member.class),
//                eq(Set.of(PermissionType.ADMIN.toString()))
//        );
//    }
//
//    @Test
//    void registerSuperAdmin_shouldCallSignupWithSuperAdminPermission() throws Exception {
//        Mockito.when(permissionRepository.findByPermissionType("SUPERADMIN"))
//                .thenReturn(java.util.Optional.of(Permission.builder()
//                        .permissionType("SUPERADMIN")
//                        .build()));
//        AuthServiceImpl spy = Mockito.spy(authService);
//
//        spy.registerSuperAdmin(signupRequestDto);
//
//        verify(spy, times(1)).signup(
//                eq(signupRequestDto),
//                any(Member.class),
//                eq(Set.of(PermissionType.SUPERADMIN.toString()))
//        );
//    }
//
//    @Test
//    void registerCashier_shouldCallSignupWithCashierPermission() throws Exception {
//        Mockito.when(permissionRepository.findByPermissionType("CASHIER"))
//                .thenReturn(java.util.Optional.of(Permission.builder()
//                        .permissionType("CASHIER")
//                        .build()));
//
//        AuthServiceImpl spy = Mockito.spy(authService);
//
//        spy.registerCashier(signupRequestDto);
//
//        verify(spy, times(1)).signup(
//                eq(signupRequestDto),
//                any(Member.class),
//                eq(Set.of(PermissionType.CASHIER.toString()))
//        );
//    }
}
