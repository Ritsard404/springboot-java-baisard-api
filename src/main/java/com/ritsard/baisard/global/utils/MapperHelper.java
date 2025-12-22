package com.ritsard.baisard.global.utils;

import java.util.function.Consumer;

public class MapperHelper {
    public static <T> void setIfValid(Consumer<T> setter, T value) {
        if (value == null) return;

        if (value instanceof String str) {
            if (!str.isBlank()) {
                setter.accept(value);
            }
        } else {
            setter.accept(value);
        }
    }

    public static boolean setIfNotBlank(Consumer<String> setter, String value) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    public static <T> boolean setIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
            return true;
        }
        return false;
    }
}
