package com.ritsard.baisard.jwt.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(
        basePackages = {"com.ritsard"}
)
@EnableConfigurationProperties
public class JwtModuleAutoConfiguration {
}
