package com.ritsard.baisard.file.repository.v2;

import com.ritsard.baisard.base.repository.BaseQueryDslRepository;
import com.ritsard.baisard.file.dto.v2.FileStatisticsDto;
import com.ritsard.baisard.file.entity.BaseFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BaseFileQueryDslRepository<T extends BaseFile, S extends FileStatisticsDto>
extends BaseQueryDslRepository<T, UUID> {
    public List<T> findUnattachedFilesOlderThan(Instant var1, int var2);

    public List<T> findDeletedFilesOlderThan(Instant var1, int var2);

    public List<T> findByEntity(String var1, UUID var2);

    public List<T> findAllByIds(List<UUID> var1);

    public long attachFilesToEntity(List<UUID> var1, String var2, UUID var3);

    public long detachFilesFromEntity(String var1, UUID var2);

    public List<S> getFileStatisticsByType();

    public List<S> getFileStatisticsByEntity();

    public List<S> getUploadStatistics(Instant var1, Instant var2);

    public Long getTotalStorageSize();
}

