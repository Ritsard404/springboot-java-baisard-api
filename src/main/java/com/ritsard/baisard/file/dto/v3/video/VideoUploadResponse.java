package com.ritsard.baisard.file.dto.v3.video;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class VideoUploadResponse {
    @Schema(description = "Uploaded file ID")
    private String fileId;

    @Schema(description = "Saved file name")
    private String filename;

    @Schema(description = "Original file name")
    private String originalFilename;

    @Schema(description = "Video stream/download URL")
    private String url;

    @Schema(description = "File MIME type")
    private String mimeType;

    @Schema(description = "File size (bytes)")
    private Long fileSize;

    @Schema(description = "File extension")
    private String fileExtension;

    @Schema(description = "Upload timestamp")
    private Instant uploadedAt;

    @Schema(description = "Video duration (seconds)")
    private Long duration;

    public static VideoUploadResponseBuilder builder() {
        return new VideoUploadResponseBuilder();
    }

    public VideoUploadResponse(String fileId, String filename, String originalFilename, String url, String mimeType, Long fileSize, String fileExtension, Instant uploadedAt, Long duration) {
        this.fileId = fileId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.url = url;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.uploadedAt = uploadedAt;
        this.duration = duration;
    }

    public static class VideoUploadResponseBuilder {
        private String fileId;
        private String filename;
        private String originalFilename;
        private String url;
        private String mimeType;
        private Long fileSize;
        private String fileExtension;
        private Instant uploadedAt;
        private Long duration;

        VideoUploadResponseBuilder() {
        }

        public VideoUploadResponseBuilder fileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public VideoUploadResponseBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public VideoUploadResponseBuilder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public VideoUploadResponseBuilder url(String url) {
            this.url = url;
            return this;
        }

        public VideoUploadResponseBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public VideoUploadResponseBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public VideoUploadResponseBuilder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public VideoUploadResponseBuilder uploadedAt(Instant uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public VideoUploadResponseBuilder duration(Long duration) {
            this.duration = duration;
            return this;
        }

        public VideoUploadResponse build() {
            return new VideoUploadResponse(this.fileId, this.filename, this.originalFilename, this.url, this.mimeType, this.fileSize, this.fileExtension, this.uploadedAt, this.duration);
        }

        public String toString() {
            return "VideoUploadResponse.VideoUploadResponseBuilder(fileId=" + this.fileId + ", filename=" + this.filename + ", originalFilename=" + this.originalFilename + ", url=" + this.url + ", mimeType=" + this.mimeType + ", fileSize=" + this.fileSize + ", fileExtension=" + this.fileExtension + ", uploadedAt=" + this.uploadedAt + ", duration=" + this.duration + ")";
        }
    }
}

