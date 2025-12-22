package com.ritsard.baisard.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccessPathType {
    GOOGLE("구글"),
    ETC("기타");

    private final String description;
}
