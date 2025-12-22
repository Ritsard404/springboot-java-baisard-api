package com.ritsard.baisard.file.controller;

import com.ritsard.baisard.file.config.SpringVideoContext;
import com.ritsard.baisard.file.dto.v3.VideoUploadRequest;
import com.ritsard.baisard.file.dto.v3.video.VideoChunkRequest;
import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import com.ritsard.baisard.file.entity.v2.FileInfo;
import com.ritsard.baisard.file.service.v4.VideoAccessService;
import com.ritsard.baisard.file.service.v4.VideoUploadService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface VideoCrudable<T, F extends FileInfo> {
    default VideoUploadService getVideoUploadService() {
        return (VideoUploadService) SpringVideoContext.getBean(VideoUploadService.class);
    }

    default VideoAccessService getVideoAccessService() {
        return (VideoAccessService) SpringVideoContext.getBean(VideoAccessService.class);
    }

    @PostMapping(
            value = {"/videos/upload"},
            consumes = {"multipart/form-data"}
    )
    @Operation(
            summary = "Upload single video",
            description = "Uploads a single video file to the server."
    )
    default ApiResponse<VideoUploadResponse> uploadVideo(@RequestPart("file") MultipartFile file, @RequestPart(value = "metadata", required = false) VideoUploadRequest metadata) throws IOException {
        return ApiResponse.ok(this.getVideoUploadService().uploadVideo(file, metadata));
    }

    @PostMapping(
            value = {"/videos/upload/multiple"},
            consumes = {"multipart/form-data"}
    )
    @Operation(
            summary = "Upload multiple videos",
            description = "Uploads multiple video files simultaneously."
    )
    default ApiResponse<List<VideoUploadResponse>> uploadMultipleVideos(@RequestParam("files") List<MultipartFile> files, @RequestPart(value = "metadata", required = false) VideoUploadRequest metadata) throws IOException {
        return ApiResponse.ok(this.getVideoUploadService().uploadMultipleVideos(files, metadata));
    }

    @PostMapping(
            value = {"/videos/upload/chunk"},
            consumes = {"multipart/form-data"}
    )
    @Operation(
            summary = "Chunked Video Upload",
            description = "Uploads a large video file in smaller chunks."
    )
    default ApiResponse<String> uploadChunk(@RequestPart("file") MultipartFile chunk, @RequestPart("info") VideoChunkRequest chunkRequest) throws IOException {
        return ApiResponse.ok(this.getVideoUploadService().uploadChunk(chunk, chunkRequest));
    }

    @GetMapping({"/videos/{videoId}"})
    @Operation(
            summary = "Stream Video",
            description = "Streams video content using partial content ranges."
    )
    default ResponseEntity<?> streamVideo(@PathVariable String videoId, @RequestHeader(value = "Range", required = false) String range) throws IOException {
        return this.getVideoAccessService().streamVideo(videoId, range);
    }

    @GetMapping({"/videos/{videoId}/info"})
    @Operation(
            summary = "Get Video Info",
            description = "Retrieves metadata for a specific video."
    )
    default ApiResponse<VideoUploadResponse> getVideoInfo(@PathVariable String videoId) throws IOException {
        return ApiResponse.ok(this.getVideoAccessService().getVideoInfo(videoId));
    }

    @DeleteMapping({"/videos/{videoId}"})
    @Operation(
            summary = "Delete Video",
            description = "Permanently deletes an uploaded video."
    )
    default ApiResponse<Void> deleteVideo(@PathVariable UUID videoId) throws IOException {
        this.getVideoUploadService().deleteVideo(videoId);
        return ApiResponse.ok();
    }
}