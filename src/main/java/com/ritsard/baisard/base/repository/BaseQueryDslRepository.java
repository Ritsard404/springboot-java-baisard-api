package com.ritsard.baisard.base.repository;

import com.ritsard.baisard.base.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BaseQueryDslRepository<T extends BaseEntity, ID> {
    Page<T> findByConditions(Object searchCondition, Pageable pageable);

    Optional<T> findActiveById(ID id);

    List<T> findActiveByIds(List<ID> ids);

    Page<T> findActiveWithPaging(Pageable pageable);

    Page<T> findActiveByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    long countActiveEntities();

    long countDeletedEntities();

    long countActiveByCreatedAtBetween(Instant startDate, Instant endDate);
}

