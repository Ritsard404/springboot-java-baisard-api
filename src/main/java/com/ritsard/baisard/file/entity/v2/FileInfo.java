/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.file.entity.v2;

import java.time.Instant;
import java.util.UUID;

public interface FileInfo {
    public UUID getFileId();

    public void setFileId(UUID var1);

    public String getFilename();

    public void setFilename(String var1);

    public String getOriginalFilename();

    public void setOriginalFilename(String var1);

    public String getFilepath();

    public void setFilepath(String var1);

    public String getUrl();

    public void setUrl(String var1);

    public String getMimeType();

    public void setMimeType(String var1);

    public Long getFileSize();

    public void setFileSize(Long var1);

    public String getFileExtension();

    public void setFileExtension(String var1);

    public Instant getUploadedAt();

    public void setUploadedAt(Instant var1);
}

