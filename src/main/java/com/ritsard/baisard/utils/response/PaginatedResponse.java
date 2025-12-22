/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 */
package com.ritsard.baisard.utils.response;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Deprecated
public class PaginatedResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent() != null ? page.getContent() : Collections.emptyList();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
    }

    public PaginatedResponse(Page<T> page, List<T> list) {
        this.content = list;
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
    }

    public <U> PaginatedResponse(Page<U> page, Function<U, T> mapper) {
        this.content = page.getContent().stream().map(mapper).collect(Collectors.toList());
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
    }

    public List<T> getContent() {
        return this.content;
    }

    public long getTotalElements() {
        return this.totalElements;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PaginatedResponse)) {
            return false;
        }
        PaginatedResponse other = (PaginatedResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTotalElements() != other.getTotalElements()) {
            return false;
        }
        if (this.getTotalPages() != other.getTotalPages()) {
            return false;
        }
        if (this.getCurrentPage() != other.getCurrentPage()) {
            return false;
        }
        if (this.getPageSize() != other.getPageSize()) {
            return false;
        }
        List<T> this$content = this.getContent();
        List<T> other$content = other.getContent();
        return !(this$content == null ? other$content != null : !((Object)this$content).equals(other$content));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PaginatedResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $totalElements = this.getTotalElements();
        result = result * 59 + (int)($totalElements >>> 32 ^ $totalElements);
        result = result * 59 + this.getTotalPages();
        result = result * 59 + this.getCurrentPage();
        result = result * 59 + this.getPageSize();
        List<T> $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : ((Object)$content).hashCode());
        return result;
    }

    public String toString() {
        return "PaginatedResponse(content=" + String.valueOf(this.getContent()) + ", totalElements=" + this.getTotalElements() + ", totalPages=" + this.getTotalPages() + ", currentPage=" + this.getCurrentPage() + ", pageSize=" + this.getPageSize() + ")";
    }

    public PaginatedResponse(List<T> content, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PaginatedResponse() {
    }
}

