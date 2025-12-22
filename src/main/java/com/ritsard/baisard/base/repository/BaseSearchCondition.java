package com.ritsard.baisard.base.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
public class BaseSearchCondition {
    private String keyword;
    private Instant startDate;
    private Instant endDate;
    @Builder.Default
    private Boolean includeDeleted = false;
}
