/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.ritsard.baisard.file.dto.v3;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "File upload result")
public class FileUploadResponse {

    @Schema(description = "File ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID fileId;

    @Schema(description = "Encrypted file ID (used for reference)", example = "EncryptedDocumentId")
    private String encryptedId;

    @Schema(description = "Saved file name", example = "550e8400-e29b-41d4-a716-446655440000.jpg")
    private String filename;

    @Schema(description = "Original file name", example = "profile.jpg")
    private String originalFilename;

    @Schema(description = "MIME type", example = "image/jpeg")
    private String mimeType;

    @Schema(description = "File size (bytes)", example = "1048576")
    private Long fileSize;

    @Schema(description = "File extension", example = "jpg")
    private String fileExtension;

    @Schema(description = "File access URL", example = "/api/files/EncryptedDocumentId")
    private String url;

    @Schema(description = "Upload timestamp", example = "2024-01-15T10:30:00Z")
    private Instant uploadedAt;

    @Schema(description = "Is attached to an entity", example = "false")
    private Boolean isAttached;

    @Schema(description = "Attached entity type", example = "product")
    private String entityType;

    @Schema(description = "Attached entity ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID entityId;

    @Schema(description = "Image width (if file is an image)", example = "1920")
    private Integer width;

    @Schema(description = "Image height (if file is an image)", example = "1080")
    private Integer height;

    @Schema(description = "Has thumbnail (if file is an image)", example = "false")
    private Boolean hasThumbnail;

    @Schema(description = "Document type (if file is a document)", example = "PDF")
    private String documentType;

    @Schema(description = "Number of pages (if file is a document)", example = "10")
    private Integer pageCount;

    @Schema(description = "Is text extractable (if file is a document)", example = "true")
    private Boolean isTextExtractable;

    public static FileUploadResponseBuilder builder() {
        return new FileUploadResponseBuilder();
    }

    public UUID getFileId() {
        return this.fileId;
    }

    public String getEncryptedId() {
        return this.encryptedId;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getOriginalFilename() {
        return this.originalFilename;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public String getUrl() {
        return this.url;
    }

    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public Boolean getIsAttached() {
        return this.isAttached;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public UUID getEntityId() {
        return this.entityId;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public Boolean getHasThumbnail() {
        return this.hasThumbnail;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public Integer getPageCount() {
        return this.pageCount;
    }

    public Boolean getIsTextExtractable() {
        return this.isTextExtractable;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public void setEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setIsAttached(Boolean isAttached) {
        this.isAttached = isAttached;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setHasThumbnail(Boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public void setIsTextExtractable(Boolean isTextExtractable) {
        this.isTextExtractable = isTextExtractable;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FileUploadResponse)) {
            return false;
        }
        FileUploadResponse other = (FileUploadResponse) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$fileSize = this.getFileSize();
        Long other$fileSize = other.getFileSize();
        if (this$fileSize == null ? other$fileSize != null : !((Object) this$fileSize).equals(other$fileSize)) {
            return false;
        }
        Boolean this$isAttached = this.getIsAttached();
        Boolean other$isAttached = other.getIsAttached();
        if (this$isAttached == null ? other$isAttached != null : !((Object) this$isAttached).equals(other$isAttached)) {
            return false;
        }
        Integer this$width = this.getWidth();
        Integer other$width = other.getWidth();
        if (this$width == null ? other$width != null : !((Object) this$width).equals(other$width)) {
            return false;
        }
        Integer this$height = this.getHeight();
        Integer other$height = other.getHeight();
        if (this$height == null ? other$height != null : !((Object) this$height).equals(other$height)) {
            return false;
        }
        Boolean this$hasThumbnail = this.getHasThumbnail();
        Boolean other$hasThumbnail = other.getHasThumbnail();
        if (this$hasThumbnail == null ? other$hasThumbnail != null : !((Object) this$hasThumbnail).equals(other$hasThumbnail)) {
            return false;
        }
        Integer this$pageCount = this.getPageCount();
        Integer other$pageCount = other.getPageCount();
        if (this$pageCount == null ? other$pageCount != null : !((Object) this$pageCount).equals(other$pageCount)) {
            return false;
        }
        Boolean this$isTextExtractable = this.getIsTextExtractable();
        Boolean other$isTextExtractable = other.getIsTextExtractable();
        if (this$isTextExtractable == null ? other$isTextExtractable != null : !((Object) this$isTextExtractable).equals(other$isTextExtractable)) {
            return false;
        }
        UUID this$fileId = this.getFileId();
        UUID other$fileId = other.getFileId();
        if (this$fileId == null ? other$fileId != null : !((Object) this$fileId).equals(other$fileId)) {
            return false;
        }
        String this$encryptedId = this.getEncryptedId();
        String other$encryptedId = other.getEncryptedId();
        if (this$encryptedId == null ? other$encryptedId != null : !this$encryptedId.equals(other$encryptedId)) {
            return false;
        }
        String this$filename = this.getFilename();
        String other$filename = other.getFilename();
        if (this$filename == null ? other$filename != null : !this$filename.equals(other$filename)) {
            return false;
        }
        String this$originalFilename = this.getOriginalFilename();
        String other$originalFilename = other.getOriginalFilename();
        if (this$originalFilename == null ? other$originalFilename != null : !this$originalFilename.equals(other$originalFilename)) {
            return false;
        }
        String this$mimeType = this.getMimeType();
        String other$mimeType = other.getMimeType();
        if (this$mimeType == null ? other$mimeType != null : !this$mimeType.equals(other$mimeType)) {
            return false;
        }
        String this$fileExtension = this.getFileExtension();
        String other$fileExtension = other.getFileExtension();
        if (this$fileExtension == null ? other$fileExtension != null : !this$fileExtension.equals(other$fileExtension)) {
            return false;
        }
        String this$url = this.getUrl();
        String other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
            return false;
        }
        Instant this$uploadedAt = this.getUploadedAt();
        Instant other$uploadedAt = other.getUploadedAt();
        if (this$uploadedAt == null ? other$uploadedAt != null : !((Object) this$uploadedAt).equals(other$uploadedAt)) {
            return false;
        }
        String this$entityType = this.getEntityType();
        String other$entityType = other.getEntityType();
        if (this$entityType == null ? other$entityType != null : !this$entityType.equals(other$entityType)) {
            return false;
        }
        UUID this$entityId = this.getEntityId();
        UUID other$entityId = other.getEntityId();
        if (this$entityId == null ? other$entityId != null : !((Object) this$entityId).equals(other$entityId)) {
            return false;
        }
        String this$documentType = this.getDocumentType();
        String other$documentType = other.getDocumentType();
        return !(this$documentType == null ? other$documentType != null : !this$documentType.equals(other$documentType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FileUploadResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $fileSize = this.getFileSize();
        result = result * 59 + ($fileSize == null ? 43 : ((Object) $fileSize).hashCode());
        Boolean $isAttached = this.getIsAttached();
        result = result * 59 + ($isAttached == null ? 43 : ((Object) $isAttached).hashCode());
        Integer $width = this.getWidth();
        result = result * 59 + ($width == null ? 43 : ((Object) $width).hashCode());
        Integer $height = this.getHeight();
        result = result * 59 + ($height == null ? 43 : ((Object) $height).hashCode());
        Boolean $hasThumbnail = this.getHasThumbnail();
        result = result * 59 + ($hasThumbnail == null ? 43 : ((Object) $hasThumbnail).hashCode());
        Integer $pageCount = this.getPageCount();
        result = result * 59 + ($pageCount == null ? 43 : ((Object) $pageCount).hashCode());
        Boolean $isTextExtractable = this.getIsTextExtractable();
        result = result * 59 + ($isTextExtractable == null ? 43 : ((Object) $isTextExtractable).hashCode());
        UUID $fileId = this.getFileId();
        result = result * 59 + ($fileId == null ? 43 : ((Object) $fileId).hashCode());
        String $encryptedId = this.getEncryptedId();
        result = result * 59 + ($encryptedId == null ? 43 : $encryptedId.hashCode());
        String $filename = this.getFilename();
        result = result * 59 + ($filename == null ? 43 : $filename.hashCode());
        String $originalFilename = this.getOriginalFilename();
        result = result * 59 + ($originalFilename == null ? 43 : $originalFilename.hashCode());
        String $mimeType = this.getMimeType();
        result = result * 59 + ($mimeType == null ? 43 : $mimeType.hashCode());
        String $fileExtension = this.getFileExtension();
        result = result * 59 + ($fileExtension == null ? 43 : $fileExtension.hashCode());
        String $url = this.getUrl();
        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        Instant $uploadedAt = this.getUploadedAt();
        result = result * 59 + ($uploadedAt == null ? 43 : ((Object) $uploadedAt).hashCode());
        String $entityType = this.getEntityType();
        result = result * 59 + ($entityType == null ? 43 : $entityType.hashCode());
        UUID $entityId = this.getEntityId();
        result = result * 59 + ($entityId == null ? 43 : ((Object) $entityId).hashCode());
        String $documentType = this.getDocumentType();
        result = result * 59 + ($documentType == null ? 43 : $documentType.hashCode());
        return result;
    }

    public String toString() {
        return "FileUploadResponse(fileId=" + String.valueOf(this.getFileId()) + ", encryptedId=" + this.getEncryptedId() + ", filename=" + this.getFilename() + ", originalFilename=" + this.getOriginalFilename() + ", mimeType=" + this.getMimeType() + ", fileSize=" + this.getFileSize() + ", fileExtension=" + this.getFileExtension() + ", url=" + this.getUrl() + ", uploadedAt=" + String.valueOf(this.getUploadedAt()) + ", isAttached=" + this.getIsAttached() + ", entityType=" + this.getEntityType() + ", entityId=" + String.valueOf(this.getEntityId()) + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ", hasThumbnail=" + this.getHasThumbnail() + ", documentType=" + this.getDocumentType() + ", pageCount=" + this.getPageCount() + ", isTextExtractable=" + this.getIsTextExtractable() + ")";
    }

    public FileUploadResponse() {
    }

    public FileUploadResponse(UUID fileId, String encryptedId, String filename, String originalFilename, String mimeType, Long fileSize, String fileExtension, String url, Instant uploadedAt, Boolean isAttached, String entityType, UUID entityId, Integer width, Integer height, Boolean hasThumbnail, String documentType, Integer pageCount, Boolean isTextExtractable) {
        this.fileId = fileId;
        this.encryptedId = encryptedId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.url = url;
        this.uploadedAt = uploadedAt;
        this.isAttached = isAttached;
        this.entityType = entityType;
        this.entityId = entityId;
        this.width = width;
        this.height = height;
        this.hasThumbnail = hasThumbnail;
        this.documentType = documentType;
        this.pageCount = pageCount;
        this.isTextExtractable = isTextExtractable;
    }

    public static class FileUploadResponseBuilder {
        private UUID fileId;
        private String encryptedId;
        private String filename;
        private String originalFilename;
        private String mimeType;
        private Long fileSize;
        private String fileExtension;
        private String url;
        private Instant uploadedAt;
        private Boolean isAttached;
        private String entityType;
        private UUID entityId;
        private Integer width;
        private Integer height;
        private Boolean hasThumbnail;
        private String documentType;
        private Integer pageCount;
        private Boolean isTextExtractable;

        FileUploadResponseBuilder() {
        }

        public FileUploadResponseBuilder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public FileUploadResponseBuilder encryptedId(String encryptedId) {
            this.encryptedId = encryptedId;
            return this;
        }

        public FileUploadResponseBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public FileUploadResponseBuilder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public FileUploadResponseBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public FileUploadResponseBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public FileUploadResponseBuilder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public FileUploadResponseBuilder url(String url) {
            this.url = url;
            return this;
        }

        public FileUploadResponseBuilder uploadedAt(Instant uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public FileUploadResponseBuilder isAttached(Boolean isAttached) {
            this.isAttached = isAttached;
            return this;
        }

        public FileUploadResponseBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public FileUploadResponseBuilder entityId(UUID entityId) {
            this.entityId = entityId;
            return this;
        }

        public FileUploadResponseBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public FileUploadResponseBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public FileUploadResponseBuilder hasThumbnail(Boolean hasThumbnail) {
            this.hasThumbnail = hasThumbnail;
            return this;
        }

        public FileUploadResponseBuilder documentType(String documentType) {
            this.documentType = documentType;
            return this;
        }

        public FileUploadResponseBuilder pageCount(Integer pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public FileUploadResponseBuilder isTextExtractable(Boolean isTextExtractable) {
            this.isTextExtractable = isTextExtractable;
            return this;
        }

        public FileUploadResponse build() {
            return new FileUploadResponse(this.fileId, this.encryptedId, this.filename, this.originalFilename, this.mimeType, this.fileSize, this.fileExtension, this.url, this.uploadedAt, this.isAttached, this.entityType, this.entityId, this.width, this.height, this.hasThumbnail, this.documentType, this.pageCount, this.isTextExtractable);
        }

        public String toString() {
            return "FileUploadResponse.FileUploadResponseBuilder(fileId=" + String.valueOf(this.fileId) + ", encryptedId=" + this.encryptedId + ", filename=" + this.filename + ", originalFilename=" + this.originalFilename + ", mimeType=" + this.mimeType + ", fileSize=" + this.fileSize + ", fileExtension=" + this.fileExtension + ", url=" + this.url + ", uploadedAt=" + String.valueOf(this.uploadedAt) + ", isAttached=" + this.isAttached + ", entityType=" + this.entityType + ", entityId=" + String.valueOf(this.entityId) + ", width=" + this.width + ", height=" + this.height + ", hasThumbnail=" + this.hasThumbnail + ", documentType=" + this.documentType + ", pageCount=" + this.pageCount + ", isTextExtractable=" + this.isTextExtractable + ")";
        }
    }
}

