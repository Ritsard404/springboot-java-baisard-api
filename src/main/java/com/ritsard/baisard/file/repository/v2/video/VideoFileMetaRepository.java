package com.ritsard.baisard.file.repository.v2.video;

import com.ritsard.baisard.file.entity.v2.VideoFileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface VideoFileMetaRepository
extends JpaRepository<VideoFileMeta, UUID> {
    @Query(value="SELECT v FROM VideoFileMeta v where v.fileId=:fileId")
    public Optional<VideoFileMeta> findByFileId(UUID var1);

    public void deleteByFileId(UUID var1);
}

