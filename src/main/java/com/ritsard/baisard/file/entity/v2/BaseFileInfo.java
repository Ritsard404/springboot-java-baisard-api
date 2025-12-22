package com.ritsard.baisard.file.entity.v2;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.util.UUID;

@Embeddable
@Access(value = AccessType.FIELD)
public class BaseFileInfo
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

    public static BaseFileInfoBuilder builder() {
        return new BaseFileInfoBuilder();
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

    public BaseFileInfo() {
    }

    public BaseFileInfo(UUID fileId, String filename, String originalFilename, String filepath, String url, String mimeType, Long fileSize, String fileExtension, Instant uploadedAt) {
        this.fileId = fileId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.filepath = filepath;
        this.url = url;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.uploadedAt = uploadedAt;
    }

    public static class BaseFileInfoBuilder {
        private UUID fileId;
        private String filename;
        private String originalFilename;
        private String filepath;
        private String url;
        private String mimeType;
        private Long fileSize;
        private String fileExtension;
        private Instant uploadedAt;

        BaseFileInfoBuilder() {
        }

        public BaseFileInfoBuilder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public BaseFileInfoBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public BaseFileInfoBuilder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public BaseFileInfoBuilder filepath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public BaseFileInfoBuilder url(String url) {
            this.url = url;
            return this;
        }

        public BaseFileInfoBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public BaseFileInfoBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public BaseFileInfoBuilder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public BaseFileInfoBuilder uploadedAt(Instant uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public BaseFileInfo build() {
            return new BaseFileInfo(this.fileId, this.filename, this.originalFilename, this.filepath, this.url, this.mimeType, this.fileSize, this.fileExtension, this.uploadedAt);
        }

        public String toString() {
            return "BaseFileInfo.BaseFileInfoBuilder(fileId=" + String.valueOf(this.fileId) + ", filename=" + this.filename + ", originalFilename=" + this.originalFilename + ", filepath=" + this.filepath + ", url=" + this.url + ", mimeType=" + this.mimeType + ", fileSize=" + this.fileSize + ", fileExtension=" + this.fileExtension + ", uploadedAt=" + String.valueOf(this.uploadedAt) + ")";
        }
    }
}

