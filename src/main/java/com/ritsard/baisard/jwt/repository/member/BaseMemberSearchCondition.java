package com.ritsard.baisard.jwt.repository.member;

import com.ritsard.baisard.base.repository.BaseSearchCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
public class BaseMemberSearchCondition extends BaseSearchCondition {
    private String email;
    private String name;
    private String nickname;
    private String phoneNumber;
    private LocalDate birthdateStart;
    private LocalDate birthdateEnd;
    private Boolean hasPermissions;
    private String permissionName;
}
