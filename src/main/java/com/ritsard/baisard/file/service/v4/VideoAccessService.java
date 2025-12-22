package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface VideoAccessService {
    public ResponseEntity<?> streamVideo(String var1, String var2) throws IOException;

    public VideoUploadResponse getVideoInfo(String var1) throws IOException;
}

