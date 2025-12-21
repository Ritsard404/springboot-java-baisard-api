package com.ritsard.baisard.base.repository;

import com.ritsard.baisard.base.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public interface BaseRepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {
    List<T> findAllByIsDeletedFalse();

    List<T> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();

    List<T> findByIsDeletedFalseAndCreatedAtAfter(Instant instant);

    List<T> findByIsDeletedFalseAndCreatedAtBefore(Instant instant);
}
