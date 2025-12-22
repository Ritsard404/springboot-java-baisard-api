package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.dto.v3.VideoUploadRequest;
import com.ritsard.baisard.file.dto.v3.video.VideoChunkRequest;
import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface VideoUploadService {
    public VideoUploadResponse uploadVideo(MultipartFile var1, VideoUploadRequest var2) throws IOException;

    public List<VideoUploadResponse> uploadMultipleVideos(List<MultipartFile> var1, VideoUploadRequest var2) throws IOException;

    public String uploadChunk(MultipartFile var1, VideoChunkRequest var2) throws IOException;

    public void deleteVideo(UUID var1) throws IOException;

    public void attachVideoToEntity(UUID var1, String var2, UUID var3) throws IOException;
}

