package com.ritsard.baisard.global.config;


import com.ritsard.baisard.jwt.filter.JwtAuthenticationFilter;
import com.ritsard.baisard.jwt.generator.TokenProvider;
import com.ritsard.baisard.jwt.redis.MemberRedisService;
import com.ritsard.baisard.utils.log.LoggingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String appName;

    private List<String> permitAllPaths() {
        String basePath = "/api/" + appName;

        return List.of(
                "/swagger-ui/**",
//                "/**",
                "/v3/**",
                "/api/documents/**",
                "/api-docs/**",
                "/api/images/**",
                basePath + "/auth/**",
                basePath + "/public/**"
//                basePath + "/auth/login",
//                basePath + "/auth/signup",
//                basePath + "/auth/sign-up/user",
//                basePath + "/auth/user/sign-up",
//                basePath + "/auth/verify",
//                basePath + "/auth/default/**",
//                basePath + "/files/**",
//                basePath + "/reservations",
//                basePath + "/reservations/preview",
//                basePath + "/reservations/breakdown",
//                basePath + "/**/files/**",
//                basePath + "/reviews/top-reviews",
//                basePath + "/reviews",
//                basePath + "/reviews/*",
//                basePath + "/articles",
//                basePath + "/articles/*",
//                basePath + "/reviews/{id}/comments",
//                basePath + "/address/search",
//                basePath + "/payments/success",
//                basePath + "/payments/fail",
//
//                basePath + "/sms/*",
//                basePath + "/course/search",
//                basePath + "/comments/course/**"
        );
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenProvider tokenProvider,
                                                           MemberRedisService redisService,
                                                           LoggingService loggingService) {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, redisService, loggingService);
        filter.setPermitAllPaths(permitAllPaths());

        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        String basePath = "/api/" + appName;

        http
                .csrf(CsrfConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    permitAllPaths().forEach(path -> authorize.requestMatchers(path).permitAll());

                    // Access control by permission
                    authorize
                            .requestMatchers(basePath + "/admin/**").hasAuthority("ADMIN")
                            .requestMatchers(basePath + "/common/**").authenticated()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Authentication is required");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("You do not have access permission\n");
                        })
                );

        return http.build();
    }

    // BCrypt encoder for password encryption
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins
        configuration.setAllowedOriginPatterns(List.of("*"));

        // List of allowed frontend domains
//        configuration.setAllowedOrigins(List.of(
//                "http://localhost:3000",
//                "http://localhost:3001",
//                "http://localhost:3002",
//                "http://localhost:3003",
//                "http://localhost:80",
//                "http://localhost:8080",
//                "http://localhost:11302",
//                "http://localhost:11306"
////                 운영 도메인 설정
////                "https://",
////                "https://www."
////                 웹소켓 설정
////                "wss://",
////                "wss://www."
//        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
