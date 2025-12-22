package com.ritsard.baisard.jwt.utils.permission;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionTypeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({PermissionTypeProvider.class})
    public PermissionTypeProvider defaultPermissionTypeProvider() {
        return () -> new IPermissionType[]{new IPermissionType() {
            public String name() {
                return "USER";
            }

            public String getDescription() {
                return "General users";
            }
        }, new IPermissionType() {
            public String name() {
                return "ADMIN";
            }

            public String getDescription() {
                return "Admin";
            }
        }};
    }
}