package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.config.FileUploadConfigSupport;
import com.ritsard.baisard.file.dto.v2.FileStatisticsDto;
import com.ritsard.baisard.file.dto.v3.FileUploadRequest;
import com.ritsard.baisard.file.dto.v3.FileUploadResponse;
import com.ritsard.baisard.file.entity.BaseFile;
import com.ritsard.baisard.file.entity.v2.BaseFileInfo;
import com.ritsard.baisard.file.entity.v2.FileInfo;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.file.repository.v2.BaseFileRepository;
import com.ritsard.baisard.file.service.v3.FilePreStorageService;
import com.ritsard.baisard.file.utils.FileIdUtils;
import com.ritsard.baisard.utils.exceptions.ImageFileIsTooBigException;
import com.ritsard.baisard.utils.exceptions.files.InvalidFileTypeException;
import com.ritsard.baisard.utils.helper.UUIDManager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class EmbeddedFileUploadService<T extends FileInfo> {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedFileUploadService.class);
    private final FilePreStorageService fileStorageService;
    private final FileUploadConfigSupport config;
    private final BaseFileRepository<BaseFile, FileStatisticsDto> baseFileRepository;
    private final Map<String, T> temporaryFiles = new ConcurrentHashMap();
    private Class<T> fileInfoClass;

    public void setFileInfoClass(Class<T> fileInfoClass) {
        this.fileInfoClass = fileInfoClass;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<T> getFilesByIds(List<String> fileIds) {
        if (fileIds != null && !fileIds.isEmpty()) {
            List<T> files = new ArrayList();

            for (String fileId : fileIds) {
                T fileInfo = (T) (this.temporaryFiles.remove(fileId));
                if (fileInfo != null) {
                    files.add(fileInfo);
                    log.info("\ud83d\udcce 파일 가져옴: {}", fileInfo.getFileId());
                } else {
                    log.warn("⚠️ 파일을 찾을 수 없음: {}", fileId);
                }
            }

            return files;
        } else {
            return new ArrayList();
        }
    }

    public List<T> attachAndMoveFiles(List<String> fileIds, String entityType, UUID entityId) {
        if (fileIds != null && !fileIds.isEmpty()) {
            List<T> movedFiles = new ArrayList();

            for (String fileId : fileIds) {
                T fileInfo = (T) (this.temporaryFiles.get(fileId));
                if (fileInfo != null) {
                    try {
                        String newPath = this.moveFileToFinalLocation(fileInfo, entityType);
                        fileInfo.setFilepath(newPath);
                        BaseFile baseFile = this.createBaseFileFromFileInfo(fileInfo, entityType, entityId);
                        this.baseFileRepository.save(baseFile);
                        this.temporaryFiles.remove(fileId);
                        movedFiles.add(fileInfo);
                        log.info("\ud83d\udce6 파일 이동 및 BaseFile 저장 완료: {} → {}", fileId, newPath);
                    } catch (Exception e) {
                        log.error("파일 처리 실패: {}", fileId, e);
                        movedFiles.add(fileInfo);
                    }
                } else {
                    log.warn("⚠️ 임시 저장소에서 파일을 찾을 수 없음: {}", fileId);
                }
            }

            return movedFiles;
        } else {
            return new ArrayList();
        }
    }

    private BaseFile createBaseFileFromFileInfo(T fileInfo, String entityType, UUID entityId) {
        return ((BaseFile.BaseFileBuilder) ((BaseFile.BaseFileBuilder) ((BaseFile.BaseFileBuilder) BaseFile.builder().uuidBaseFile(fileInfo.getFileId()).filename(fileInfo.getFilename()).originalFilename(fileInfo.getOriginalFilename()).filepath(fileInfo.getFilepath()).mimeType(fileInfo.getMimeType()).fileSize(fileInfo.getFileSize()).fileExtension(fileInfo.getFileExtension()).url(fileInfo.getUrl()).entityType(entityType.toLowerCase()).entityId(entityId).isAttached(true).attachedAt(Instant.now()).isDeleted(false)).createdAt(Instant.now())).updatedAt(Instant.now())).build();
    }

    private String moveFileToFinalLocation(T fileInfo, String entityType) {
        try {
            String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String var10000 = entityType.toLowerCase();
            String newFolder = var10000 + "/" + yearMonth;
            String filename = fileInfo.getFilename();
            String newPath = this.fileStorageService.moveFile(fileInfo.getFilepath(), filename, newFolder);
            log.debug("파일 이동: {} -> {}", fileInfo.getFilepath(), newPath);
            return newPath;
        } catch (Exception e) {
            log.error("파일 이동 중 오류 발생", e);
            return fileInfo.getFilepath();
        }
    }

    public FileUploadResponse uploadFile(MultipartFile file, FileUploadRequest metadata) {
        this.validateFile(file);

        try {
            String folder = "temp";
            String filename = this.generateFilename(file);
            String filepath = this.fileStorageService.storeFile(file, filename, folder);
            T fileInfo = this.createFileInfo(file, filename, filepath, (String) null);
            String fileId = FileIdUtils.encode(fileInfo.getFileId());
            String fileUrl = this.fileStorageService.getFileUrl(fileId);
            fileInfo.setUrl(fileUrl);
            this.temporaryFiles.put(fileId, fileInfo);
            log.info("\ud83d\udcc1 파일 업로드 완료 (ID: {}): {} - 현재 임시 파일 수: {}", new Object[]{fileId, fileInfo.getFileId(), this.temporaryFiles.size()});
            return this.toResponse(fileInfo, fileId);
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private T createFileInfo(MultipartFile file, String filename, String filepath, String url) {
        try {
            T fileInfo;
            if (this.fileInfoClass != null && ImageFileInfo.class.isAssignableFrom(this.fileInfoClass)) {
                ImageFileInfo.ImageFileInfoBuilder builder = ImageFileInfo.builder().fileId(UUIDManager.generateUUIDv7()).filename(filename).originalFilename(file.getOriginalFilename()).filepath(filepath).url(url).mimeType(file.getContentType()).fileSize(file.getSize()).fileExtension(this.getFileExtension(file.getOriginalFilename())).uploadedAt(Instant.now()).hasThumbnail(false).quality((double) 1.0F);

                try {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                    if (image != null) {
                        builder.originalWidth(image.getWidth()).originalHeight(image.getHeight());
                    }
                } catch (Exception e) {
                    log.warn("이미지 메타데이터 추출 실패", e);
                }

                fileInfo = (T) builder.build();
            } else {
                fileInfo = (T) BaseFileInfo.builder().fileId(UUIDManager.generateUUIDv7()).filename(filename).originalFilename(file.getOriginalFilename()).filepath(filepath).url(url).mimeType(file.getContentType()).fileSize(file.getSize()).fileExtension(this.getFileExtension(file.getOriginalFilename())).uploadedAt(Instant.now()).build();
            }

            return fileInfo;
        } catch (Exception e) {
            throw new RuntimeException("FileInfo 인스턴스 생성 실패", e);
        }
    }

    public List<FileUploadResponse> uploadMultipleFiles(List<MultipartFile> files, FileUploadRequest metadata) {
        return (List) files.stream().map((file) -> this.uploadFile(file, metadata)).collect(Collectors.toList());
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Transactional
    public void attachFilesToEntity(List<String> fileIds, Consumer<T> addFileFunction) {
        for (String fileId : fileIds) {
            T fileInfo = (T) (this.temporaryFiles.get(fileId));
            if (fileInfo != null) {
                addFileFunction.accept(fileInfo);
                this.temporaryFiles.remove(fileId);
                log.info("\ud83d\udcce 파일 연결: {}", fileInfo.getFileId());
            }
        }

    }

    public FileUploadResponse getFileInfo(String fileId) {
        T fileInfo = (T) (this.temporaryFiles.get(fileId));
        if (fileInfo != null) {
            return this.toResponse(fileInfo, fileId);
        } else {
            FileInfo dbInfo = this.getFileInfoFromDatabase(fileId);
            if (dbInfo != null) {
                return this.toResponse((T) dbInfo, fileId);
            } else {
                throw new NoSuchElementException("파일을 찾을 수 없습니다: " + fileId);
            }
        }
    }

    public T getFileInfoDirect(String fileId) {
        return (T) (this.temporaryFiles.get(fileId));
    }

    public FileInfo getFileInfoFromDatabase(String fileId) {
        try {
            UUID uuid = FileIdUtils.decode(fileId);
            Optional<BaseFile> baseFile = this.baseFileRepository.findActiveById(uuid);
            if (baseFile.isPresent()) {
                return this.convertBaseFileToFileInfo((BaseFile) baseFile.get());
            }
        } catch (Exception e) {
            log.warn("파일 ID로 DB 조회 실패: {}", fileId, e);
        }

        return null;
    }

    private FileInfo convertBaseFileToFileInfo(BaseFile baseFile) {
        return (FileInfo) (baseFile.getMimeType() != null && baseFile.getMimeType().startsWith("image/") ? ImageFileInfo.builder().fileId(baseFile.getUuidBaseFile()).filename(baseFile.getFilename()).originalFilename(baseFile.getOriginalFilename()).filepath(baseFile.getFilepath()).url(baseFile.getUrl()).mimeType(baseFile.getMimeType()).fileSize(baseFile.getFileSize()).fileExtension(baseFile.getFileExtension()).uploadedAt(baseFile.getCreatedAt()).hasThumbnail(false).quality((double) 1.0F).build() : BaseFileInfo.builder().fileId(baseFile.getUuidBaseFile()).filename(baseFile.getFilename()).originalFilename(baseFile.getOriginalFilename()).filepath(baseFile.getFilepath()).url(baseFile.getUrl()).mimeType(baseFile.getMimeType()).fileSize(baseFile.getFileSize()).fileExtension(baseFile.getFileExtension()).uploadedAt(baseFile.getCreatedAt()).build());
    }

    public void deleteFile(String fileId) {
        T fileInfo = (T) (this.temporaryFiles.remove(fileId));
        if (fileInfo != null) {
            try {
                this.fileStorageService.deleteFile(fileInfo.getFilepath());
                log.info("\ud83d\uddd1️ 파일 삭제: {}", fileInfo.getFileId());
            } catch (Exception e) {
                log.error("파일 삭제 실패", e);
            }
        } else {
            try {
                UUID uuid = FileIdUtils.decode(fileId);
                Optional<BaseFile> baseFile = this.baseFileRepository.findActiveById(uuid);
                if (baseFile.isPresent()) {
                    BaseFile file = (BaseFile) baseFile.get();
                    file.setDeleted(true);
                    file.setUpdatedAt(Instant.now());
                    this.baseFileRepository.save(file);
                    this.fileStorageService.deleteFile(file.getFilepath());
                    log.info("\ud83d\uddd1️ DB 파일 삭제: {}", uuid);
                }
            } catch (Exception e) {
                log.error("DB 파일 삭제 실패: {}", fileId, e);
            }
        }

    }

    public List<FileUploadResponse> getFilesByEntity(String entityType, UUID entityId) {
        try {
            List<BaseFile> files = this.baseFileRepository.findByEntityTypeAndEntityIdAndIsDeletedFalse(entityType.toLowerCase(), entityId);
            return (List) files.stream().map((file) -> {
                FileInfo fileInfo = this.convertBaseFileToFileInfo(file);
                String fileId = FileIdUtils.encode(file.getUuidBaseFile());
                return this.toResponse((T) fileInfo, fileId);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("엔티티 파일 목록 조회 실패: {} - {}", new Object[]{entityType, entityId, e});
            return new ArrayList();
        }
    }

    protected void validateFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > this.config.getMaxUploadSize()) {
                long var10002 = this.config.getMaxUploadSize() / 1024L;
                throw new ImageFileIsTooBigException("최대 파일 크기 초과: " + var10002 / 1024L + "MB");
            } else {
                String ext = this.getFileExtension(file.getOriginalFilename());
                if (ext == null || !this.config.getAllowedExtensions().contains(ext.toLowerCase())) {
                    throw new InvalidFileTypeException("허용되지 않는 파일 확장자입니다: " + ext);
                }
            }
        } else {
            throw new InvalidFileTypeException("파일이 비어있습니다.");
        }
    }

    protected FileUploadResponse toResponse(T fileInfo, String fileId) {
        FileUploadResponse.FileUploadResponseBuilder builder = FileUploadResponse.builder().fileId(fileInfo.getFileId()).encryptedId(fileId).filename(fileInfo.getFilename()).originalFilename(fileInfo.getOriginalFilename()).mimeType(fileInfo.getMimeType()).fileSize(fileInfo.getFileSize()).fileExtension(fileInfo.getFileExtension()).url(fileInfo.getUrl()).uploadedAt(fileInfo.getUploadedAt()).isAttached(!this.temporaryFiles.containsKey(fileId));
        if (fileInfo instanceof ImageFileInfo imageInfo) {
            builder.width(imageInfo.getOriginalWidth()).height(imageInfo.getOriginalHeight()).hasThumbnail(imageInfo.getHasThumbnail());
        }

        return builder.build();
    }

    protected String generateFilename(MultipartFile file) {
        String ext = this.getFileExtension(file.getOriginalFilename());
        String var10000 = String.valueOf(UUIDManager.generateUUIDv7());
        return var10000 + (ext != null ? "." + ext : "");
    }

    protected String getFileExtension(String filename) {
        return filename != null && filename.contains(".") ? filename.substring(filename.lastIndexOf(46) + 1).toLowerCase() : null;
    }

    @Transactional
    public void cleanupTemporaryFiles(int hoursOld) {
        Instant cutoffTime = Instant.now().minusSeconds((long) hoursOld * 3600L);
        List<String> toRemove = (List) this.temporaryFiles.entrySet().stream().filter((entry) -> ((FileInfo) entry.getValue()).getUploadedAt().isBefore(cutoffTime)).map(Map.Entry::getKey).collect(Collectors.toList());
        toRemove.forEach((fileId) -> {
            T fileInfo = (T) (this.temporaryFiles.remove(fileId));
            if (fileInfo != null) {
                try {
                    this.fileStorageService.deleteFile(fileInfo.getFilepath());
                    log.info("\ud83e\uddf9 임시 파일 정리: {}", fileInfo.getFileId());
                } catch (Exception e) {
                    log.error("임시 파일 삭제 실패", e);
                }
            }

        });
        if (!toRemove.isEmpty()) {
            log.info("\ud83e\uddf9 {}개의 임시 파일이 정리되었습니다.", toRemove.size());
        }

    }

    public Map<String, Object> getTemporaryFilesStatus() {
        return Map.of("totalFiles", this.temporaryFiles.size(), "oldestFile", this.temporaryFiles.values().stream().map(FileInfo::getUploadedAt).min(Instant::compareTo).orElse((Instant) null), "totalSize", this.temporaryFiles.values().stream().mapToLong(FileInfo::getFileSize).sum());
    }

    public EmbeddedFileUploadService(final FilePreStorageService fileStorageService, final FileUploadConfigSupport config, final BaseFileRepository<BaseFile, FileStatisticsDto> baseFileRepository) {
        this.fileStorageService = fileStorageService;
        this.config = config;
        this.baseFileRepository = baseFileRepository;
    }
}
