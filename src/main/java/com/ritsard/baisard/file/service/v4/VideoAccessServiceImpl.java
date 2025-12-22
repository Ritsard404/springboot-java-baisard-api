package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import com.ritsard.baisard.file.entity.v2.VideoFileInfo;
import com.ritsard.baisard.file.entity.v2.VideoFileMeta;
import com.ritsard.baisard.file.repository.v2.video.VideoFileMetaRepository;
import com.ritsard.baisard.file.utils.FileIdUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class VideoAccessServiceImpl implements VideoAccessService {
    private final FileStorageService storage;
    private final VideoFileMetaRepository metaRepo;

    @Transactional(
            readOnly = true
    )
    public ResponseEntity<Resource> streamVideo(String videoIdBase64, String rangeHeader) {
        UUID videoId = FileIdUtils.decode(videoIdBase64);
        VideoFileMeta meta = (VideoFileMeta) this.metaRepo.findByFileId(videoId).orElseThrow(() -> new NoSuchElementException("Video not found: " + String.valueOf(videoId)));

        Resource video;
        try {
            video = this.storage.loadFrom(meta.getStorageNode(), meta.getStoragePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load video resource", e);
        }

        long contentLength;
        try {
            contentLength = video.contentLength();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read content length", e);
        }

        MediaType contentType = (MediaType) MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM);
        if (!StringUtils.hasText(rangeHeader)) {
            return ((ResponseEntity.BodyBuilder) ResponseEntity.ok().header("Accept-Ranges", new String[]{"bytes"})).contentType(contentType).contentLength(contentLength).body(video);
        } else {
            String[] parts = rangeHeader.replace("bytes=", "").split("-");
            long start = Long.parseLong(parts[0]);
            long end = parts.length > 1 && !parts[1].isEmpty() ? Long.parseLong(parts[1]) : contentLength - 1L;
            long rangeLength = end - start + 1L;

            byte[] data;
            try {
                data = this.storage.readRange(video, start, rangeLength);
            } catch (IOException e) {
                throw new IllegalStateException("Video streaming error", e);
            }

            return ((ResponseEntity.BodyBuilder) ((ResponseEntity.BodyBuilder) ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).header("Content-Range", new String[]{String.format("bytes %d-%d/%d", start, end, contentLength)})).header("Accept-Ranges", new String[]{"bytes"})).contentType(contentType).contentLength(rangeLength).body(new ByteArrayResource(data));
        }
    }

    @Transactional(
            readOnly = true
    )
    public VideoUploadResponse getVideoInfo(String videoIdBase64) throws IOException {
        UUID videoId = FileIdUtils.decode(videoIdBase64);
        VideoFileMeta meta = (VideoFileMeta) this.metaRepo.findByFileId(videoId).orElseThrow(() -> new NoSuchElementException("Video not found: " + String.valueOf(videoId)));
        Resource video = this.storage.loadFrom(meta.getStorageNode(), meta.getStoragePath());

        long size;
        try {
            size = video.contentLength();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read content length", e);
        }

        String mime = ((MediaType) MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM)).toString();
        Path finalPath = Paths.get(meta.getStoragePath());
        VideoFileInfo info = this.storage.buildFileInfo(videoId, finalPath, size, mime);
        return VideoUploadResponse.builder().fileId(FileIdUtils.encode(videoId)).filename(info.getFilename()).originalFilename(info.getOriginalFilename()).url(info.getUrl()).mimeType(info.getMimeType()).fileSize(info.getFileSize()).fileExtension(info.getFileExtension()).uploadedAt(info.getUploadedAt()).duration(meta.getDuration()).build();
    }

    public VideoAccessServiceImpl(final FileStorageService storage, final VideoFileMetaRepository metaRepo) {
        this.storage = storage;
        this.metaRepo = metaRepo;
    }
}
