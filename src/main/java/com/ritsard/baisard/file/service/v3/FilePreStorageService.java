/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.lodong.utilsmodule.exceptions.files.InvalidFileTypeException
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ritsard.baisard.file.service.v3;

import com.ritsard.baisard.file.config.FileUploadConfigSupport;
import com.ritsard.baisard.utils.exceptions.files.InvalidFileTypeException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class FilePreStorageService {
    private static final Logger log = LoggerFactory.getLogger(FilePreStorageService.class);
    private final FileUploadConfigSupport config;
    private Path rootLocation;
    private String rootLocationString;
    @Value(value="${spring.application.name:default}")
    private String applicationName;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(this.config.getResolvedBasePath()).toAbsolutePath();
        this.rootLocationString = this.rootLocation.toString();
        try {
            Files.createDirectories(this.rootLocation);
            log.info("📁 File storage location: {}", this.rootLocationString);
            log.info("🔧 File system type: {}", this.rootLocation.getFileSystem().getClass().getName());
            log.info("🔧 Provider: {}", this.rootLocation.getFileSystem().provider().getClass().getName());
        } catch (IOException e) {
            throw new InvalidFileTypeException("Failed to initialize file storage location: " + e.getMessage());
        }
    }

    public String storeFile(MultipartFile file, String filename, String folder) {
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("Empty file cannot be stored.");
        }
        try {
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path targetPath = this.rootLocation.resolve(Paths.get(folder, dateDir)).normalize();
            Files.createDirectories(targetPath);
            Path destinationFile = targetPath.resolve(filename).normalize().toAbsolutePath();
            if (!destinationFile.toAbsolutePath().toString().startsWith(this.rootLocationString)) {
                throw new InvalidFileTypeException("File path is not valid.");
            }
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return this.getRelativePath(destinationFile);
        } catch (IOException e) {
            log.error("📜 File storage error: {}", e.getMessage(), e);
            throw new InvalidFileTypeException("Failed to store file: " + e.getMessage());
        }
    }

    private String getRelativePath(Path absolutePath) {
        try {
            if (absolutePath.getFileSystem().equals(this.rootLocation.getFileSystem())) {
                return this.rootLocation.relativize(absolutePath).toString().replace("\\", "/");
            }
        } catch (Exception e) {
            log.debug("Relativize failed → fallback: {}", e.getMessage());
        }
        return this.calculateStringBasedRelativePath(absolutePath);
    }

    private String calculateStringBasedRelativePath(Path absolutePath) {
        String abs = absolutePath.toAbsolutePath().toString();
        if (abs.startsWith(this.rootLocationString)) {
            String rel = abs.substring(this.rootLocationString.length());
            while (rel.startsWith("/") || rel.startsWith("\\")) {
                rel = rel.substring(1);
            }
            return rel.replace("\\", "/");
        }
        return absolutePath.getFileName().toString();
    }

    public String moveFile(String oldPath, String filename, String newFolder) {
        try {
            Path source = this.rootLocation.resolve(oldPath).normalize();
            if (!Files.exists(source)) {
                log.error("File to move does not exist: {}", oldPath);
                return oldPath;
            }
            Path targetDir = this.rootLocation.resolve(newFolder).normalize();
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(filename).normalize().toAbsolutePath();
            if (!target.toAbsolutePath().toString().startsWith(this.rootLocationString)) {
                throw new InvalidFileTypeException("Invalid file path");
            }
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            this.cleanupEmptyDirectories(source.getParent());
            String newPath = this.getRelativePath(target);
            log.debug("📦 File moved successfully: {} → {}", oldPath, newPath);
            return newPath;
        } catch (IOException e) {
            log.error("File move error: {} → {}", oldPath, newFolder, e);
            return oldPath;
        }
    }

    public String copyFile(String sourcePath, String filename, String targetFolder) {
        try {
            Path source = this.rootLocation.resolve(sourcePath).normalize();
            if (!Files.exists(source)) {
                throw new InvalidFileTypeException("File to copy does not exist: " + sourcePath);
            }
            Path targetDir = this.rootLocation.resolve(targetFolder);
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(filename).normalize().toAbsolutePath();
            if (!target.toAbsolutePath().toString().startsWith(this.rootLocationString)) {
                throw new InvalidFileTypeException("Invalid file path");
            }
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return this.getRelativePath(target);
        } catch (IOException e) {
            log.error("File copy error: {}", e.getMessage());
            throw new InvalidFileTypeException("File copy error: " + e.getMessage());
        }
    }

    private void cleanupEmptyDirectories(Path directory) {
        try {
            while (directory != null && !directory.equals(this.rootLocation) && directory.startsWith(this.rootLocation) && this.isDirectoryEmpty(directory)) {
                Files.delete(directory);
                log.debug("🗑 Empty directory deleted: {}", directory);
                directory = directory.getParent();
            }
        } catch (IOException e) {
            log.debug("Directory cleanup error: {}", e.getMessage());
        }
    }

    private boolean isDirectoryEmpty(Path directory) throws IOException {
        try (Stream<Path> entries = Files.list(directory)) {
            return !entries.findFirst().isPresent();
        }
    }

    public String getFileUrl(String encryptedId) {
        String projectName = this.applicationName;
        if (projectName == null || projectName.isBlank()) {
            projectName = "default";
        }
        return String.format("/api/%s/files/%s", projectName, encryptedId);
    }

    public void deleteFile(String filepath) {
        try {
            Path file = this.loadFile(filepath);
            boolean deleted = Files.deleteIfExists(file);
            if (deleted) {
                log.debug("🗑 File deleted: {}", filepath);
                this.cleanupEmptyDirectories(file.getParent());
            }
        } catch (IOException e) {
            log.error("File deletion error: {}", e.getMessage());
        }
    }

    public boolean fileExists(String filepath) {
        try {
            Path file = this.loadFile(filepath);
            return Files.exists(file) && Files.isReadable(file);
        } catch (Exception e) {
            return false;
        }
    }

    public Path loadFile(String filepath) {
        Path file = this.rootLocation.resolve(filepath).normalize().toAbsolutePath();
        if (!file.toAbsolutePath().toString().startsWith(this.rootLocationString)) {
            throw new InvalidFileTypeException("File path is not valid.");
        }
        return file;
    }

    public long getFileSize(String filepath) {
        try {
            Path file = this.loadFile(filepath);
            return Files.size(file);
        } catch (IOException e) {
            log.error("Failed to get file size: {}", e.getMessage());
            return -1L;
        }
    }

    public void cleanupTempFiles(int hoursOld) {
        final Path tempDir = this.rootLocation.resolve("temp");
        if (!Files.exists(tempDir)) {
            return;
        }
        try {
            final long cutoffTime = System.currentTimeMillis() - (long)(hoursOld * 60 * 60) * 1000L;
            Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.lastModifiedTime().toMillis() < cutoffTime) {
                        Files.delete(file);
                        log.info("🗑 Old temp file deleted: {}", file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (FilePreStorageService.this.isDirectoryEmpty(dir) && !dir.equals(tempDir)) {
                        Files.delete(dir);
                        log.debug("🗑 Empty directory deleted: {}", dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Temp file cleanup error", e);
        }
    }

    public FilePreStorageService(FileUploadConfigSupport config) {
        this.config = config;
    }
}


