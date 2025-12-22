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
@Access(value=AccessType.FIELD)
public class ImageFileInfo
implements FileInfo {
    @Column(name="file_id")
    private UUID fileId;
    @Column(name="filename", length=255)
    private String filename;
    @Column(name="original_filename", length=255)
    private String originalFilename;
    @Column(name="filepath", length=1000)
    private String filepath;
    @Column(name="url", length=1000)
    private String url;
    @Column(name="mime_type", length=100)
    private String mimeType;
    @Column(name="file_size")
    private Long fileSize;
    @Column(name="file_extension", length=20)
    private String fileExtension;
    @Column(name="uploaded_at")
    private Instant uploadedAt;
    @Column(name="original_width")
    private Integer originalWidth;
    @Column(name="original_height")
    private Integer originalHeight;
    @Column(name="has_thumbnail")
    private Boolean hasThumbnail;
    @Column(name="quality")
    private Double quality;
    @Column(name="resized_filepath")
    private String resizedFilepath;

    public static ImageFileInfoBuilder builder() {
        return new ImageFileInfoBuilder();
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

    public Integer getOriginalWidth() {
        return this.originalWidth;
    }

    public Integer getOriginalHeight() {
        return this.originalHeight;
    }

    public Boolean getHasThumbnail() {
        return this.hasThumbnail;
    }

    public Double getQuality() {
        return this.quality;
    }

    public String getResizedFilepath() {
        return this.resizedFilepath;
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

    public void setOriginalWidth(Integer originalWidth) {
        this.originalWidth = originalWidth;
    }

    public void setOriginalHeight(Integer originalHeight) {
        this.originalHeight = originalHeight;
    }

    public void setHasThumbnail(Boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public void setResizedFilepath(String resizedFilepath) {
        this.resizedFilepath = resizedFilepath;
    }

    public ImageFileInfo() {
    }

    public ImageFileInfo(UUID fileId, String filename, String originalFilename, String filepath, String url, String mimeType, Long fileSize, String fileExtension, Instant uploadedAt, Integer originalWidth, Integer originalHeight, Boolean hasThumbnail, Double quality, String resizedFilepath) {
        this.fileId = fileId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.filepath = filepath;
        this.url = url;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.uploadedAt = uploadedAt;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.hasThumbnail = hasThumbnail;
        this.quality = quality;
        this.resizedFilepath = resizedFilepath;
    }

    public static class ImageFileInfoBuilder {
        private UUID fileId;
        private String filename;
        private String originalFilename;
        private String filepath;
        private String url;
        private String mimeType;
        private Long fileSize;
        private String fileExtension;
        private Instant uploadedAt;
        private Integer originalWidth;
        private Integer originalHeight;
        private Boolean hasThumbnail;
        private Double quality;
        private String resizedFilepath;

        ImageFileInfoBuilder() {
        }

        public ImageFileInfoBuilder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public ImageFileInfoBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public ImageFileInfoBuilder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public ImageFileInfoBuilder filepath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public ImageFileInfoBuilder url(String url) {
            this.url = url;
            return this;
        }

        public ImageFileInfoBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public ImageFileInfoBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public ImageFileInfoBuilder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public ImageFileInfoBuilder uploadedAt(Instant uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public ImageFileInfoBuilder originalWidth(Integer originalWidth) {
            this.originalWidth = originalWidth;
            return this;
        }

        public ImageFileInfoBuilder originalHeight(Integer originalHeight) {
            this.originalHeight = originalHeight;
            return this;
        }

        public ImageFileInfoBuilder hasThumbnail(Boolean hasThumbnail) {
            this.hasThumbnail = hasThumbnail;
            return this;
        }

        public ImageFileInfoBuilder quality(Double quality) {
            this.quality = quality;
            return this;
        }

        public ImageFileInfoBuilder resizedFilepath(String resizedFilepath) {
            this.resizedFilepath = resizedFilepath;
            return this;
        }

        public ImageFileInfo build() {
            return new ImageFileInfo(this.fileId, this.filename, this.originalFilename, this.filepath, this.url, this.mimeType, this.fileSize, this.fileExtension, this.uploadedAt, this.originalWidth, this.originalHeight, this.hasThumbnail, this.quality, this.resizedFilepath);
        }

        public String toString() {
            return "ImageFileInfo.ImageFileInfoBuilder(fileId=" + String.valueOf(this.fileId) + ", filename=" + this.filename + ", originalFilename=" + this.originalFilename + ", filepath=" + this.filepath + ", url=" + this.url + ", mimeType=" + this.mimeType + ", fileSize=" + this.fileSize + ", fileExtension=" + this.fileExtension + ", uploadedAt=" + String.valueOf(this.uploadedAt) + ", originalWidth=" + this.originalWidth + ", originalHeight=" + this.originalHeight + ", hasThumbnail=" + this.hasThumbnail + ", quality=" + this.quality + ", resizedFilepath=" + this.resizedFilepath + ")";
        }
    }
}

