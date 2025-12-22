/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.Id
 *  jakarta.persistence.Table
 */
package com.ritsard.baisard.file.entity.v2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="video_file_meta")
public class VideoFileMeta {
    @Id
    @Column(name="uuid_video_meta")
    private UUID uuidVideoMeta;
    @Column(name="file_id")
    private UUID fileId;
    @Column(name="status")
    private String status;
    @Column(name="error_message")
    private String errorMessage;
    @Column(name="progress")
    private Integer progress;
    @Column(name="last_updated")
    private Instant lastUpdated;
    @Column(name="duration")
    private Long duration;
    @Column(name="storage_node", length=100)
    private String storageNode;
    @Column(name="storage_path", length=1000)
    private String storagePath;

    public static VideoFileMetaBuilder builder() {
        return new VideoFileMetaBuilder();
    }

    public VideoFileMeta(UUID uuidVideoMeta, UUID fileId, String status, String errorMessage, Integer progress, Instant lastUpdated, Long duration, String storageNode, String storagePath) {
        this.uuidVideoMeta = uuidVideoMeta;
        this.fileId = fileId;
        this.status = status;
        this.errorMessage = errorMessage;
        this.progress = progress;
        this.lastUpdated = lastUpdated;
        this.duration = duration;
        this.storageNode = storageNode;
        this.storagePath = storagePath;
    }

    public VideoFileMeta() {
    }

    public UUID getUuidVideoMeta() {
        return this.uuidVideoMeta;
    }

    public UUID getFileId() {
        return this.fileId;
    }

    public String getStatus() {
        return this.status;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Integer getProgress() {
        return this.progress;
    }

    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    public Long getDuration() {
        return this.duration;
    }

    public String getStorageNode() {
        return this.storageNode;
    }

    public String getStoragePath() {
        return this.storagePath;
    }

    public void setUuidVideoMeta(UUID uuidVideoMeta) {
        this.uuidVideoMeta = uuidVideoMeta;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setStorageNode(String storageNode) {
        this.storageNode = storageNode;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VideoFileMeta)) {
            return false;
        }
        VideoFileMeta other = (VideoFileMeta)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$progress = this.getProgress();
        Integer other$progress = other.getProgress();
        if (this$progress == null ? other$progress != null : !((Object)this$progress).equals(other$progress)) {
            return false;
        }
        Long this$duration = this.getDuration();
        Long other$duration = other.getDuration();
        if (this$duration == null ? other$duration != null : !((Object)this$duration).equals(other$duration)) {
            return false;
        }
        UUID this$uuidVideoMeta = this.getUuidVideoMeta();
        UUID other$uuidVideoMeta = other.getUuidVideoMeta();
        if (this$uuidVideoMeta == null ? other$uuidVideoMeta != null : !((Object)this$uuidVideoMeta).equals(other$uuidVideoMeta)) {
            return false;
        }
        UUID this$fileId = this.getFileId();
        UUID other$fileId = other.getFileId();
        if (this$fileId == null ? other$fileId != null : !((Object)this$fileId).equals(other$fileId)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$errorMessage = this.getErrorMessage();
        String other$errorMessage = other.getErrorMessage();
        if (this$errorMessage == null ? other$errorMessage != null : !this$errorMessage.equals(other$errorMessage)) {
            return false;
        }
        Instant this$lastUpdated = this.getLastUpdated();
        Instant other$lastUpdated = other.getLastUpdated();
        if (this$lastUpdated == null ? other$lastUpdated != null : !((Object)this$lastUpdated).equals(other$lastUpdated)) {
            return false;
        }
        String this$storageNode = this.getStorageNode();
        String other$storageNode = other.getStorageNode();
        if (this$storageNode == null ? other$storageNode != null : !this$storageNode.equals(other$storageNode)) {
            return false;
        }
        String this$storagePath = this.getStoragePath();
        String other$storagePath = other.getStoragePath();
        return !(this$storagePath == null ? other$storagePath != null : !this$storagePath.equals(other$storagePath));
    }

    protected boolean canEqual(Object other) {
        return other instanceof VideoFileMeta;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $progress = this.getProgress();
        result = result * 59 + ($progress == null ? 43 : ((Object)$progress).hashCode());
        Long $duration = this.getDuration();
        result = result * 59 + ($duration == null ? 43 : ((Object)$duration).hashCode());
        UUID $uuidVideoMeta = this.getUuidVideoMeta();
        result = result * 59 + ($uuidVideoMeta == null ? 43 : ((Object)$uuidVideoMeta).hashCode());
        UUID $fileId = this.getFileId();
        result = result * 59 + ($fileId == null ? 43 : ((Object)$fileId).hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $errorMessage = this.getErrorMessage();
        result = result * 59 + ($errorMessage == null ? 43 : $errorMessage.hashCode());
        Instant $lastUpdated = this.getLastUpdated();
        result = result * 59 + ($lastUpdated == null ? 43 : ((Object)$lastUpdated).hashCode());
        String $storageNode = this.getStorageNode();
        result = result * 59 + ($storageNode == null ? 43 : $storageNode.hashCode());
        String $storagePath = this.getStoragePath();
        result = result * 59 + ($storagePath == null ? 43 : $storagePath.hashCode());
        return result;
    }

    public String toString() {
        return "VideoFileMeta(uuidVideoMeta=" + String.valueOf(this.getUuidVideoMeta()) + ", fileId=" + String.valueOf(this.getFileId()) + ", status=" + this.getStatus() + ", errorMessage=" + this.getErrorMessage() + ", progress=" + this.getProgress() + ", lastUpdated=" + String.valueOf(this.getLastUpdated()) + ", duration=" + this.getDuration() + ", storageNode=" + this.getStorageNode() + ", storagePath=" + this.getStoragePath() + ")";
    }

    public static class VideoFileMetaBuilder {
        private UUID uuidVideoMeta;
        private UUID fileId;
        private String status;
        private String errorMessage;
        private Integer progress;
        private Instant lastUpdated;
        private Long duration;
        private String storageNode;
        private String storagePath;

        VideoFileMetaBuilder() {
        }

        public VideoFileMetaBuilder uuidVideoMeta(UUID uuidVideoMeta) {
            this.uuidVideoMeta = uuidVideoMeta;
            return this;
        }

        public VideoFileMetaBuilder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public VideoFileMetaBuilder status(String status) {
            this.status = status;
            return this;
        }

        public VideoFileMetaBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public VideoFileMetaBuilder progress(Integer progress) {
            this.progress = progress;
            return this;
        }

        public VideoFileMetaBuilder lastUpdated(Instant lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public VideoFileMetaBuilder duration(Long duration) {
            this.duration = duration;
            return this;
        }

        public VideoFileMetaBuilder storageNode(String storageNode) {
            this.storageNode = storageNode;
            return this;
        }

        public VideoFileMetaBuilder storagePath(String storagePath) {
            this.storagePath = storagePath;
            return this;
        }

        public VideoFileMeta build() {
            return new VideoFileMeta(this.uuidVideoMeta, this.fileId, this.status, this.errorMessage, this.progress, this.lastUpdated, this.duration, this.storageNode, this.storagePath);
        }

        public String toString() {
            return "VideoFileMeta.VideoFileMetaBuilder(uuidVideoMeta=" + String.valueOf(this.uuidVideoMeta) + ", fileId=" + String.valueOf(this.fileId) + ", status=" + this.status + ", errorMessage=" + this.errorMessage + ", progress=" + this.progress + ", lastUpdated=" + String.valueOf(this.lastUpdated) + ", duration=" + this.duration + ", storageNode=" + this.storageNode + ", storagePath=" + this.storagePath + ")";
        }
    }
}

