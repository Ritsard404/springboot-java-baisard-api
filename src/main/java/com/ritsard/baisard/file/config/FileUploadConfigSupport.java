package com.ritsard.baisard.file.config;

import java.util.List;

public interface FileUploadConfigSupport {
    public String getLinuxBasePath();

    public String getWindowsBasePath();

    public long getMaxUploadSize();

    public List<String> getAllowedExtensions();

    public String getUrlPrefix();

    public String getActiveProfile();

    default public String getResolvedBasePath() {
        return "local".equalsIgnoreCase(this.getActiveProfile()) ? this.getWindowsBasePath() : this.getLinuxBasePath();
    }

    default public boolean isProd() {
        return "prod".equalsIgnoreCase(this.getActiveProfile());
    }
}

