package com.ritsard.baisard.global.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationHelper {
    public static Pageable getPageable(Integer page, Integer size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(direction.toUpperCase()).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}
