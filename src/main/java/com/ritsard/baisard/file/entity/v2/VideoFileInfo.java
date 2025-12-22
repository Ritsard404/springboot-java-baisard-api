/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.persistence.Access
 *  jakarta.persistence.AccessType
 *  jakarta.persistence.Column
 *  jakarta.persistence.Embeddable
 */
package com.ritsard.baisard.file.entity.v2;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.util.UUID;

@Embeddable
@Access(value = AccessType.FIELD)
public class VideoFileInfo
        implements FileInfo {
    @Column(name = "file_id")
    private UUID fileId;
    @Column(name = "filename", length = 255)
    private String filename;
    @Column(name = "original_filename", length = 255)
    private String originalFilename;
    @Column(name = "filepath", length = 1000)
    private String filepath;
    @Column(name = "url", length = 1000)
    private String url;
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "file_extension", length = 20)
    private String fileExtension;
    @Column(name = "uploaded_at")
    private Instant uploadedAt;
    @Column(name = "duration")
    private Long duration;

    public static VideoFileInfoBuilder builder() {
        return new VideoFileInfoBuilder();
    }

    @Override
    public UUID getFileId() {
        return this.fileId;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public Long getFileSize() {
        return this.fileSize;
    }

    @Override
    public String getFileExtension() {
        return this.fileExtension;
    }

    @Override
    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public Long getDuration() {
        return this.duration;
    }

    @Override
    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @Override
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public VideoFileInfo() {
    }

    public VideoFileInfo(UUID fileId, String filename, String originalFilename, String filepath, String url, String mimeType, Long fileSize, String fileExtension, Instant uploadedAt, Long duration) {
        this.fileId = fileId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.filepath = filepath;
        this.url = url;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.uploadedAt = uploadedAt;
        this.duration = duration;
    }

    public static class VideoFileInfoBuilder {
        private UUID fileId;
        private String filename;
        private String originalFilename;
        private String filepath;
        private String url;
        private String mimeType;
        private Long fileSize;
        private String fileExtension;
        private Instant uploadedAt;
        private Long duration;

        VideoFileInfoBuilder() {
        }

        public VideoFileInfoBuilder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public VideoFileInfoBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public VideoFileInfoBuilder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public VideoFileInfoBuilder filepath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public VideoFileInfoBuilder url(String url) {
            this.url = url;
            return this;
        }

        public VideoFileInfoBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public VideoFileInfoBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public VideoFileInfoBuilder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public VideoFileInfoBuilder uploadedAt(Instant uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public VideoFileInfoBuilder duration(Long duration) {
            this.duration = duration;
            return this;
        }

        public VideoFileInfo build() {
            return new VideoFileInfo(this.fileId, this.filename, this.originalFilename, this.filepath, this.url, this.mimeType, this.fileSize, this.fileExtension, this.uploadedAt, this.duration);
        }

        public String toString() {
            return "VideoFileInfo.VideoFileInfoBuilder(fileId=" + this.fileId + ", filename=" + this.filename + ", originalFilename=" + this.originalFilename + ", filepath=" + this.filepath + ", url=" + this.url + ", mimeType=" + this.mimeType + ", fileSize=" + this.fileSize + ", fileExtension=" + this.fileExtension + ", uploadedAt=" + this.uploadedAt + ", duration=" + this.duration + ")";
        }
    }
}

