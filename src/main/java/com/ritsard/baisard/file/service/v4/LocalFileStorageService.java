package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.config.FileUploadConfigSupport;
import com.ritsard.baisard.file.entity.v2.VideoFileInfo;
import com.ritsard.baisard.file.utils.FileIdUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class LocalFileStorageService
        implements FileStorageService {
    private final FileUploadConfigSupport config;
    private String tempDir;
    private String finalDir;

    @PostConstruct
    public void init() throws IOException {
        String base = this.config.getResolvedBasePath();
        this.tempDir = Paths.get(base, "video-temp").toString();
        this.finalDir = Paths.get(base, "video").toString();
        Files.createDirectories(Paths.get(this.tempDir, new String[0]), new FileAttribute[0]);
        Files.createDirectories(Paths.get(this.finalDir, new String[0]), new FileAttribute[0]);
    }

    @Override
    public String getTempRoot() {
        return this.tempDir;
    }

    @Override
    public String getFinalRoot() {
        return this.finalDir;
    }

    @Override
    public Path storeTemp(UUID fileId, MultipartFile file) throws IOException {
        Path dir = Paths.get(this.tempDir, fileId.toString());
        Files.createDirectories(dir, new FileAttribute[0]);
        Path target = dir.resolve(file.getOriginalFilename());
        file.transferTo(target);
        return target;
    }

    @Override
    public void storeChunk(UUID fileId, int index, MultipartFile chunk) throws IOException {
        Path dir = Paths.get(this.tempDir, fileId.toString());
        Files.createDirectories(dir, new FileAttribute[0]);
        Path chunkFile = dir.resolve("chunk-" + index);
        chunk.transferTo(chunkFile);
    }

    @Override
    public Path mergeChunks(UUID fileId, int totalChunks) throws IOException {
        Path dir = Paths.get(this.tempDir, fileId.toString());
        Path merged = dir.resolve(fileId.toString());
        try (OutputStream out = Files.newOutputStream(merged, StandardOpenOption.CREATE, StandardOpenOption.WRITE);) {
            for (int i = 0; i < totalChunks; ++i) {
                Files.copy(dir.resolve("chunk-" + i), out);
            }
        }
        return merged;
    }

    @Override
    public Path moveTempToFinal(UUID fileId) throws IOException {
        Path srcDir = Paths.get(this.tempDir, fileId.toString());
        Path file = Files.list(srcDir).filter(p -> !p.getFileName().toString().startsWith("chunk-")).findFirst().orElseThrow(() -> new NoSuchFileException("No uploaded file for " + String.valueOf(fileId)));
        Path destDir = Paths.get(this.finalDir, fileId.toString());
        Files.createDirectories(destDir, new FileAttribute[0]);
        return Files.move(file, destDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteFinal(UUID fileId) throws IOException {
        Path dir = Paths.get(this.finalDir, fileId.toString());
        FileSystemUtils.deleteRecursively((Path) dir);
    }

    @Override
    public VideoFileInfo buildFileInfo(UUID fileId, Path finalPath, long size, String mimeType) {
        String prefix;
        String filename = finalPath.getFileName().toString();
        String ext = "";
        int idx = filename.lastIndexOf(46);
        if (idx > 0) {
            ext = filename.substring(idx + 1);
        }
        String url = (String) ((prefix = this.config.getUrlPrefix()).endsWith("/") ? prefix : prefix + "/") + "files/video/" + FileIdUtils.encode(fileId);
        return VideoFileInfo.builder().fileId(fileId).filename(filename).originalFilename(filename).filepath(finalPath.toString()).url(url).mimeType(mimeType).fileSize(size).fileExtension(ext).uploadedAt(Instant.now()).duration(null).build();
    }

    @Override
    public Resource loadAsResource(UUID fileId) throws IOException {
        Path dir = Paths.get(this.finalDir, fileId.toString());
        Path file = Files.list(dir).findFirst().orElseThrow(() -> new NoSuchFileException("File not found: " + String.valueOf(fileId)));
        UrlResource res = new UrlResource(file.toUri());
        if (!res.exists() || !res.isReadable()) {
            throw new RuntimeException("Could not read file: " + String.valueOf(fileId));
        }
        return res;
    }

    @Override
    public byte[] readRange(Resource resource, long start, long length) throws IOException {
        try (InputStream is = resource.getInputStream();) {
            is.skip(start);
            byte[] buf = new byte[(int) length];
            int read = is.read(buf);
            byte[] byArray = (long) read < length ? Arrays.copyOf(buf, read) : buf;
            return byArray;
        }
    }

    @Override
    public Resource loadFrom(String storageNode, String storagePath) throws IOException {
        UrlResource res = new UrlResource(Paths.get(storagePath, new String[0]).toUri());
        if (!res.exists() || !res.isReadable()) {
            throw new RuntimeException("File not readable: " + storagePath);
        }
        return res;
    }

    public LocalFileStorageService(FileUploadConfigSupport config) {
        this.config = config;
    }
}

