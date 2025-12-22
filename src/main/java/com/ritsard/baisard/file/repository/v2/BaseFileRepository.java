/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.lodong.basemodule.repository.BaseRepository
 */
package com.ritsard.baisard.file.repository.v2;

import com.ritsard.baisard.base.repository.BaseRepository;
import com.ritsard.baisard.file.dto.v2.FileStatisticsDto;
import com.ritsard.baisard.file.entity.BaseFile;

import java.util.List;
import java.util.UUID;

public interface BaseFileRepository<T extends BaseFile, S extends FileStatisticsDto>
        extends BaseRepository<T, UUID>,
        BaseFileQueryDslRepository<T, S> {
    public List<T> findByEntityTypeAndEntityIdAndIsDeletedFalse(String var1, UUID var2);
}

