package com.ritsard.baisard.file.utils;

import com.ritsard.baisard.file.dto.v3.FileUploadResponse;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ImageProcessUtils {
    private final DomainResolver domainResolver;

    private String toAbsoluteUrl(String relativePath) {
        if (!StringUtils.hasText((String)relativePath)) {
            return null;
        }
        return this.domainResolver.resolveCurrentDomain() + relativePath;
    }

    public FileUploadResponse convertToResponse(ImageFileInfo image) {
        if (image == null) {
            return null;
        }
        return FileUploadResponse.builder().fileId(image.getFileId()).filename(image.getFilename()).originalFilename(image.getOriginalFilename()).mimeType(image.getMimeType()).fileSize(image.getFileSize()).fileExtension(image.getFileExtension()).url(this.toAbsoluteUrl(image.getUrl())).uploadedAt(image.getUploadedAt()).width(image.getOriginalWidth()).height(image.getOriginalHeight()).hasThumbnail(image.getHasThumbnail()).isAttached(false).build();
    }

    public List<FileUploadResponse> convertToResponse(List<ImageFileInfo> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream().filter(Objects::nonNull).map(this::convertToResponse).toList();
    }

    public String extractUrl(ImageFileInfo image) {
        if (image == null) {
            return null;
        }
        return this.toAbsoluteUrl(image.getUrl());
    }

    public List<String> extractUrls(List<ImageFileInfo> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream().filter(Objects::nonNull).map(ImageFileInfo::getUrl).map(this::toAbsoluteUrl).filter(Objects::nonNull).toList();
    }

    public Optional<FileUploadResponse> getFirstImage(List<ImageFileInfo> images) {
        if (images == null || images.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.convertToResponse(images.get(0)));
    }

    public Optional<String> getFirstImageUrl(List<ImageFileInfo> images) {
        if (images == null || images.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.extractUrl(images.get(0)));
    }

    public int countImages(List<ImageFileInfo> images) {
        return images != null ? images.size() : 0;
    }

    public boolean hasImages(List<ImageFileInfo> images) {
        return images != null && !images.isEmpty();
    }

    public List<String> convertToAbsoluteUrls(List<String> relativePaths) {
        if (relativePaths == null || relativePaths.isEmpty()) {
            return Collections.emptyList();
        }
        return relativePaths.stream().map(this::toAbsoluteUrl).filter(Objects::nonNull).toList();
    }

    public ImageProcessUtils(DomainResolver domainResolver) {
        this.domainResolver = domainResolver;
    }
}

