package com.ritsard.baisard.utils.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {"com.ritsard.baisard"})
@EnableConfigurationProperties
public class UtilsModuleAutoConfiguration {
}

