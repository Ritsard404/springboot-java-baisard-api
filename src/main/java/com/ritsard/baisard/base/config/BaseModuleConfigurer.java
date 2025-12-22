package com.ritsard.baisard.base.config;

public interface BaseModuleConfigurer {
    default String toSnakeCase(String input) {
        return input != null && !input.isEmpty() ? input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase() : input;
    }
}
