package com.ritsard.baisard.domain.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminRepositoryQuery {
    Page<MyCashiersDto> findMyCashiersProjected(BooleanBuilder where, Pageable pageable);
}
