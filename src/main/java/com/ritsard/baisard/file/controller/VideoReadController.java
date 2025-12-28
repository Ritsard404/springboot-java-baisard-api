package com.ritsard.baisard.file.controller;

import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import com.ritsard.baisard.file.service.v4.VideoAccessService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "videos",
        description = "Video Read API Endpoints"
)
@RestController
@RequestMapping({"files/video"})
@Hidden
public class VideoReadController {
    private final VideoAccessService videoAccessService;

    @GetMapping({"/{encryptedId}"})
    @Operation(
            summary = "Stream video content"
    )
    public ResponseEntity<?> streamVideo(@PathVariable String encryptedId, @RequestHeader(value = "Range", required = false) String range) throws IOException {
        return this.videoAccessService.streamVideo(encryptedId, range);
    }

    @GetMapping({"/{encryptedId}/info"})
    @Operation(
            summary = "Get video metadata"
    )
    public ApiResponse<VideoUploadResponse> getVideoInfo(@PathVariable String encryptedId) throws IOException {
        return ApiResponse.ok(this.videoAccessService.getVideoInfo(encryptedId));
    }

    public VideoReadController(final VideoAccessService videoAccessService) {
        this.videoAccessService = videoAccessService;
    }
}