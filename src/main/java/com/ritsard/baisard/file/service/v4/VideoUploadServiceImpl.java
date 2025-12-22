
package com.ritsard.baisard.file.service.v4;


import com.ritsard.baisard.file.dto.v3.VideoUploadRequest;
import com.ritsard.baisard.file.dto.v3.video.VideoChunkRequest;
import com.ritsard.baisard.file.dto.v3.video.VideoUploadResponse;
import com.ritsard.baisard.file.entity.v2.VideoFileInfo;
import com.ritsard.baisard.file.entity.v2.VideoFileMeta;
import com.ritsard.baisard.file.repository.v2.video.VideoFileMetaRepository;
import com.ritsard.baisard.file.utils.FileIdUtils;
import com.ritsard.baisard.utils.helper.UUIDManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class VideoUploadServiceImpl
        implements VideoUploadService {
    private final FileStorageService storage;
    private final VideoFileMetaRepository metaRepo;

    @Override
    @Transactional
    public VideoUploadResponse uploadVideo(MultipartFile file, VideoUploadRequest metadata) {
        try {
            UUID fileId = UUIDManager.generateUUIDv7();
            Path tempPath = this.storage.storeTemp(fileId, file);
            Path finalPath = this.storage.moveTempToFinal(fileId);
            String nodeId = InetAddress.getLocalHost().getHostName();
            VideoFileMeta meta = VideoFileMeta.builder().uuidVideoMeta(UUIDManager.generateUUIDv7()).fileId(fileId).status("complete").progress(100).lastUpdated(Instant.now()).storageNode(nodeId).storagePath(finalPath.toString()).build();
            this.metaRepo.save(meta);
            VideoFileInfo info = this.storage.buildFileInfo(fileId, finalPath, file.getSize(), file.getContentType());
            return VideoUploadResponse.builder().fileId(FileIdUtils.encode(fileId)).filename(info.getFilename()).originalFilename(info.getOriginalFilename()).url(info.getUrl()).mimeType(info.getMimeType()).fileSize(info.getFileSize()).fileExtension(info.getFileExtension()).uploadedAt(info.getUploadedAt()).duration(info.getDuration()).build();
        } catch (IOException e) {
            throw new RuntimeException("\uc601\uc0c1 \uc5c5\ub85c\ub4dc \uc2e4\ud328 / Video upload failed", e);
        }
    }

    @Override
    public List<VideoUploadResponse> uploadMultipleVideos(List<MultipartFile> files, VideoUploadRequest metadata) {
        ArrayList<VideoUploadResponse> responses = new ArrayList<VideoUploadResponse>();
        for (MultipartFile file : files) {
            responses.add(this.uploadVideo(file, metadata));
        }
        return responses;
    }

    @Override
    @Transactional
    public void attachVideoToEntity(UUID fileId, String entityType, UUID entityId) throws IOException {
        Path tempDir = Paths.get(this.storage.getTempRoot(), fileId.toString());
        if (Files.exists(tempDir, new LinkOption[0]) && Files.list(tempDir).anyMatch(p -> !p.getFileName().toString().startsWith("chunk-"))) {
            Path source = Files.list(tempDir).filter(p -> !p.getFileName().toString().startsWith("chunk-")).findFirst().orElseThrow(() -> new NoSuchFileException("No merged video for " + String.valueOf(fileId)));
            Path finalDir = Paths.get(this.storage.getFinalRoot(), entityType, entityId.toString());
            Files.createDirectories(finalDir, new FileAttribute[0]);
            Path target = finalDir.resolve(source.getFileName());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            VideoFileMeta meta = this.metaRepo.findByFileId(fileId).orElseThrow(() -> new NoSuchElementException("Meta missing for " + String.valueOf(fileId)));
            meta.setStoragePath(target.toString());
            meta.setLastUpdated(Instant.now());
            this.metaRepo.save(meta);
        }
    }

    @Override
    @Transactional
    public String uploadChunk(MultipartFile chunk, VideoChunkRequest req) {
        try {
            this.storage.storeChunk(req.getFileId(), req.getChunkIndex(), chunk);
            int progress = (int) ((long) (req.getChunkIndex() + 1) * 100L / (long) req.getTotalChunks());
            VideoFileMeta meta = this.metaRepo.findByFileId(req.getFileId()).orElseGet(() -> VideoFileMeta.builder().uuidVideoMeta(UUID.randomUUID()).fileId(req.getFileId()).build());
            meta.setStatus("uploading");
            meta.setProgress(progress);
            meta.setLastUpdated(Instant.now());
            this.metaRepo.save(meta);
            if (req.getChunkIndex() + 1 == req.getTotalChunks()) {
                UUID fileId = req.getFileId();
                Path mergedPath = this.storage.mergeChunks(fileId, req.getTotalChunks());
                Path finalPath = this.storage.moveTempToFinal(fileId);
                String nodeId = InetAddress.getLocalHost().getHostName();
                meta.setStorageNode(nodeId);
                meta.setStoragePath(finalPath.toString());
                meta.setStatus("complete");
                meta.setProgress(100);
                meta.setLastUpdated(Instant.now());
                this.metaRepo.save(meta);
                return FileIdUtils.encode(fileId);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("\uccad\ud06c \uc5c5\ub85c\ub4dc \uc2e4\ud328 / Chunk upload failed", e);
        }
    }

    @Override
    @Transactional
    public void deleteVideo(UUID videoId) {
        try {
            this.storage.deleteFinal(videoId);
            this.metaRepo.deleteByFileId(videoId);
        } catch (IOException e) {
            throw new RuntimeException("\uc601\uc0c1 \uc0ad\uc81c \uc2e4\ud328 / Video deletion failed", e);
        }
    }

    public VideoUploadServiceImpl(FileStorageService storage, VideoFileMetaRepository metaRepo) {
        this.storage = storage;
        this.metaRepo = metaRepo;
    }
}

