/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.ritsard.baisard.file.dto.v3;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description="Video upload metadata")
public class VideoUploadRequest {

    @Schema(description="Video title", example="Summer Vacation 2025")
    private String title;

    @Schema(description="Video description", example="Our family trip to the beach")
    private String description;

    @Schema(description="Tags list", example="[\"vacation\",\"family\"]")
    private String[] tags;

    public static VideoUploadRequestBuilder builder() {
        return new VideoUploadRequestBuilder();
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public VideoUploadRequest() {
    }

    public VideoUploadRequest(String title, String description, String[] tags) {
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    public static class VideoUploadRequestBuilder {
        private String title;
        private String description;
        private String[] tags;

        VideoUploadRequestBuilder() {
        }

        public VideoUploadRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public VideoUploadRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VideoUploadRequestBuilder tags(String[] tags) {
            this.tags = tags;
            return this;
        }

        public VideoUploadRequest build() {
            return new VideoUploadRequest(this.title, this.description, this.tags);
        }

        public String toString() {
            return "VideoUploadRequest.VideoUploadRequestBuilder(title=" + this.title + ", description=" + this.description + ", tags=" + Arrays.deepToString(this.tags) + ")";
        }
    }
}

