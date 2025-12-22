package com.ritsard.baisard.file.dto.v3;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "File upload metadata")
public class FileUploadRequest {
    @Schema(description = "File description", example = "Profile image")
    private String description;

    @Schema(description = "File category", example = "profile")
    private String category;

    @Schema(description = "File tags", example = "{\"type\": \"avatar\", \"version\": \"v1\"}")
    private Map<String, String> tags;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;

    public static FileUploadRequestBuilder builder() {
        return new FileUploadRequestBuilder();
    }

    public String getDescription() {
        return this.description;
    }

    public String getCategory() {
        return this.category;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FileUploadRequest)) {
            return false;
        }
        FileUploadRequest other = (FileUploadRequest) o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$category = this.getCategory();
        String other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) {
            return false;
        }
        Map<String, String> this$tags = this.getTags();
        Map<String, String> other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !((Object) this$tags).equals(other$tags)) {
            return false;
        }
        Map<String, Object> this$metadata = this.getMetadata();
        Map<String, Object> other$metadata = other.getMetadata();
        return !(this$metadata == null ? other$metadata != null : !((Object) this$metadata).equals(other$metadata));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FileUploadRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $category = this.getCategory();
        result = result * 59 + ($category == null ? 43 : $category.hashCode());
        Map<String, String> $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : ((Object) $tags).hashCode());
        Map<String, Object> $metadata = this.getMetadata();
        result = result * 59 + ($metadata == null ? 43 : ((Object) $metadata).hashCode());
        return result;
    }

    public String toString() {
        return "FileUploadRequest(description=" + this.getDescription() + ", category=" + this.getCategory() + ", tags=" + String.valueOf(this.getTags()) + ", metadata=" + String.valueOf(this.getMetadata()) + ")";
    }

    public FileUploadRequest() {
    }

    public FileUploadRequest(String description, String category, Map<String, String> tags, Map<String, Object> metadata) {
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.metadata = metadata;
    }

    public static class FileUploadRequestBuilder {
        private String description;
        private String category;
        private Map<String, String> tags;
        private Map<String, Object> metadata;

        FileUploadRequestBuilder() {
        }

        public FileUploadRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FileUploadRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public FileUploadRequestBuilder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public FileUploadRequestBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public FileUploadRequest build() {
            return new FileUploadRequest(this.description, this.category, this.tags, this.metadata);
        }

        public String toString() {
            return "FileUploadRequest.FileUploadRequestBuilder(description=" + this.description + ", category=" + this.category + ", tags=" + String.valueOf(this.tags) + ", metadata=" + String.valueOf(this.metadata) + ")";
        }
    }
}

