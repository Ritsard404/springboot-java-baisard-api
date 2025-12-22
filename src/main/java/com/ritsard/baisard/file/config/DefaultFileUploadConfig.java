package com.ritsard.baisard.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultFileUploadConfig
        implements FileUploadConfigSupport {
    @Value(value = "${spring.profiles.active:local}")
    private String activeProfile;

    @Override
    public String getLinuxBasePath() {
        return "/app/files";
    }

    @Override
    public String getWindowsBasePath() {
        return "C:/temp/files";
    }

    @Override
    public long getMaxUploadSize() {
        return 0x6400000L;
    }

    @Override
    public List<String> getAllowedExtensions() {
        return List.of("jpg", "jpeg", "png", "gif", "webp", "heic", "heif", "pdf", "doc", "docx", "xls", "xlsx", "mp4", "mov", "zip", "rar", "7z", "txt", "csv");
    }

    @Override
    public String getUrlPrefix() {
        return "";
    }

    @Override
    public String getActiveProfile() {
        return this.activeProfile;
    }
}

