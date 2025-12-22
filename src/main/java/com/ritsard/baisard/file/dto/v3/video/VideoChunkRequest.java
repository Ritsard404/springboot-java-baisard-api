/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.ritsard.baisard.file.dto.v3.video;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Chunk upload info")
public class VideoChunkRequest {

    @Schema(description = "ID of the video being uploaded", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID fileId;

    @Schema(description = "Current chunk index (0-based)", example = "0")
    private int chunkIndex;

    @Schema(description = "Total number of chunks", example = "5")
    private int totalChunks;

    public static VideoChunkRequestBuilder builder() {
        return new VideoChunkRequestBuilder();
    }

    public UUID getFileId() {
        return this.fileId;
    }

    public int getChunkIndex() {
        return this.chunkIndex;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public VideoChunkRequest() {
    }

    public VideoChunkRequest(UUID fileId, int chunkIndex, int totalChunks) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
    }

    public static class VideoChunkRequestBuilder {
        private UUID fileId;
        private int chunkIndex;
        private int totalChunks;

        VideoChunkRequestBuilder() {
        }

        public VideoChunkRequestBuilder fileId(UUID fileId) {
            this.fileId = fileId;
            return this;
        }

        public VideoChunkRequestBuilder chunkIndex(int chunkIndex) {
            this.chunkIndex = chunkIndex;
            return this;
        }

        public VideoChunkRequestBuilder totalChunks(int totalChunks) {
            this.totalChunks = totalChunks;
            return this;
        }

        public VideoChunkRequest build() {
            return new VideoChunkRequest(this.fileId, this.chunkIndex, this.totalChunks);
        }

        public String toString() {
            return "VideoChunkRequest.VideoChunkRequestBuilder(fileId=" + String.valueOf(this.fileId) + ", chunkIndex=" + this.chunkIndex + ", totalChunks=" + this.totalChunks + ")";
        }
    }
}

