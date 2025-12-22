package com.ritsard.baisard.file.service.v4;

import com.ritsard.baisard.file.dto.v2.FileStatisticsDto;
import com.ritsard.baisard.file.entity.BaseFile;
import com.ritsard.baisard.file.entity.v2.BaseFileInfo;
import com.ritsard.baisard.file.entity.v2.FileInfo;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.file.service.v3.FilePreStorageService;
import com.ritsard.baisard.file.utils.FileIdUtils;
import com.ritsard.baisard.file.repository.v2.BaseFileRepository;
import com.ritsard.baisard.utils.log.LoggingService;


import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        readOnly = true
)
public class EmbeddedFileAccessService {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedFileAccessService.class);
    private final FilePreStorageService fileStorageService;
    private final EmbeddedFileUploadService<?> fileUploadService;
    private final LoggingService loggingService;
    private final BaseFileRepository<BaseFile, FileStatisticsDto> baseFileRepository;
    private final Map<String, FileInfo> fileCache = new ConcurrentHashMap();

    public ResponseEntity<Resource> getFile(String fileId, String disposition, String type, String ifNoneMatch, String ifModifiedSince) {
        try {
            log.debug("파일 요청: ID={}, disposition={}, type={}", new Object[]{fileId, disposition, type});
            if (!FileIdUtils.isValid(fileId)) {
                log.warn("유효하지 않은 파일 ID: {}", fileId);
                return ResponseEntity.badRequest().build();
            } else {
                FileInfo fileInfo = this.getFileInfo(fileId);
                String filePath = this.determineFilePath(fileInfo, type);
                Path path = this.fileStorageService.loadFile(filePath);
                Resource resource = new UrlResource(path.toUri());
                if (resource.exists() && resource.isReadable()) {
                    HttpHeaders headers = this.createHeaders(fileInfo, disposition);
                    String etag = this.generateETag(fileInfo);
                    String lastModified = this.generateLastModified(fileInfo);
                    if (this.isNotModified(ifNoneMatch, ifModifiedSince, etag, lastModified)) {
                        return ((ResponseEntity.BodyBuilder) ((ResponseEntity.BodyBuilder) ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag)).lastModified(fileInfo.getUploadedAt())).build();
                    } else {
                        MediaType contentType = this.determineContentType(fileInfo);
                        log.debug("파일 제공: {} ({})", fileInfo.getOriginalFilename(), contentType);
                        return ((ResponseEntity.BodyBuilder) ((ResponseEntity.BodyBuilder) ((ResponseEntity.BodyBuilder) ResponseEntity.ok().headers(headers)).eTag(etag)).lastModified(fileInfo.getUploadedAt())).contentType(contentType).contentLength(resource.contentLength()).body(resource);
                    }
                } else {
                    log.error("파일을 읽을 수 없음: {}", filePath);
                    throw new IllegalStateException("파일을 읽을 수 없습니다.");
                }
            }
        } catch (Exception e) {
            log.error("파일 접근 실패 (ID: {})", fileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Cacheable(
            value = {"fileCache"},
            key = "#fileId"
    )
    public FileInfo getFileInfo(String fileId) {
        log.debug("파일 조회 요청: {}", fileId);
        FileInfo cached = (FileInfo) this.fileCache.get(fileId);
        if (cached != null) {
            log.debug("메모리 캐시에서 파일 정보 조회: {}", fileId);
            return cached;
        } else {
            FileInfo tempInfo = this.fileUploadService.getFileInfoDirect(fileId);
            if (tempInfo != null) {
                log.debug("임시 저장소에서 파일 정보 조회: {}", fileId);
                this.fileCache.put(fileId, tempInfo);
                return tempInfo;
            } else {
                FileInfo dbInfo = this.getFileInfoFromDatabase(fileId);
                if (dbInfo != null) {
                    log.debug("DB에서 파일 조회 성공: {}", fileId);
                    this.fileCache.put(fileId, dbInfo);
                    return dbInfo;
                } else {
                    log.error("파일을 찾을 수 없음: {}", fileId);
                    throw new IllegalStateException("파일을 찾을 수 없습니다: " + fileId);
                }
            }
        }
    }

    private FileInfo getFileInfoFromDatabase(String fileId) {
        try {
            UUID uuid = FileIdUtils.decode(fileId);
            Optional<BaseFile> baseFileOpt = this.baseFileRepository.findActiveById(uuid);
            if (baseFileOpt.isPresent()) {
                BaseFile baseFile = (BaseFile) baseFileOpt.get();
                log.debug("DB에서 파일 조회 성공: {} ({})", uuid, baseFile.getOriginalFilename());
                return this.convertBaseFileToFileInfo(baseFile);
            }
        } catch (Exception e) {
            log.warn("파일 ID로 DB 조회 실패: {}", fileId, e);
        }

        return null;
    }

    private FileInfo convertBaseFileToFileInfo(BaseFile baseFile) {
        return (FileInfo) (baseFile.getMimeType() != null && baseFile.getMimeType().startsWith("image/") ? ImageFileInfo.builder().fileId(baseFile.getUuidBaseFile()).filename(baseFile.getFilename()).originalFilename(baseFile.getOriginalFilename()).filepath(baseFile.getFilepath()).url(baseFile.getUrl()).mimeType(baseFile.getMimeType()).fileSize(baseFile.getFileSize()).fileExtension(baseFile.getFileExtension()).uploadedAt(baseFile.getCreatedAt()).hasThumbnail(false).quality((double) 1.0F).build() : BaseFileInfo.builder().fileId(baseFile.getUuidBaseFile()).filename(baseFile.getFilename()).originalFilename(baseFile.getOriginalFilename()).filepath(baseFile.getFilepath()).url(baseFile.getUrl()).mimeType(baseFile.getMimeType()).fileSize(baseFile.getFileSize()).fileExtension(baseFile.getFileExtension()).uploadedAt(baseFile.getCreatedAt()).build());
    }

    private String determineFilePath(FileInfo fileInfo, String type) {
        if ("thumbnail".equalsIgnoreCase(type) && fileInfo instanceof ImageFileInfo imageInfo) {
            if (imageInfo.getHasThumbnail() && imageInfo.getResizedFilepath() != null) {
                log.debug("썸네일 파일 제공: {}", imageInfo.getResizedFilepath());
                return imageInfo.getResizedFilepath();
            }
        }

        return fileInfo.getFilepath();
    }

    private HttpHeaders createHeaders(FileInfo fileInfo, String disposition) {
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition;
        if ("attachment".equalsIgnoreCase(disposition)) {
            contentDisposition = ContentDisposition.attachment().filename(fileInfo.getOriginalFilename()).build();
        } else {
            contentDisposition = ContentDisposition.inline().filename(fileInfo.getOriginalFilename()).build();
        }

        headers.setContentDisposition(contentDisposition);
        if (this.isImageFile(fileInfo.getMimeType())) {
            headers.setCacheControl(CacheControl.maxAge(30L, TimeUnit.DAYS).cachePublic());
        } else {
            headers.setCacheControl(CacheControl.maxAge(1L, TimeUnit.HOURS).cachePrivate());
        }

        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, HEAD, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Range");
        return headers;
    }

    private String generateETag(FileInfo fileInfo) {
        String var10000 = fileInfo.getFileId().toString().replace("-", "");
        return "\"" + var10000 + "-" + fileInfo.getFileSize() + "-" + fileInfo.getUploadedAt().toEpochMilli() + "\"";
    }

    private String generateLastModified(FileInfo fileInfo) {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(fileInfo.getUploadedAt().atOffset(ZoneOffset.UTC));
    }

    private boolean isNotModified(String ifNoneMatch, String ifModifiedSince, String etag, String lastModified) {
        if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            log.debug("ETag 일치 - 304 Not Modified 반환");
            return true;
        } else if (ifModifiedSince != null && ifModifiedSince.equals(lastModified)) {
            log.debug("Last-Modified 일치 - 304 Not Modified 반환");
            return true;
        } else {
            return false;
        }
    }

    private MediaType determineContentType(FileInfo fileInfo) {
        try {
            if (fileInfo.getMimeType() != null && !fileInfo.getMimeType().isBlank()) {
                return MediaType.parseMediaType(fileInfo.getMimeType());
            }

            String extension = fileInfo.getFileExtension();
            if (extension != null) {
                return this.getMediaTypeByExtension(extension);
            }
        } catch (Exception e) {
            log.warn("Content-Type 파싱 실패: {}", fileInfo.getMimeType(), e);
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private MediaType getMediaTypeByExtension(String extension) {
        MediaType var10000;
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                var10000 = MediaType.IMAGE_JPEG;
                break;
            case "png":
                var10000 = MediaType.IMAGE_PNG;
                break;
            case "gif":
                var10000 = MediaType.IMAGE_GIF;
                break;
            case "webp":
                var10000 = MediaType.valueOf("image/webp");
                break;
            case "pdf":
                var10000 = MediaType.APPLICATION_PDF;
                break;
            case "txt":
                var10000 = MediaType.TEXT_PLAIN;
                break;
            case "html":
            case "htm":
                var10000 = MediaType.TEXT_HTML;
                break;
            case "css":
                var10000 = MediaType.valueOf("text/css");
                break;
            case "js":
                var10000 = MediaType.valueOf("application/javascript");
                break;
            case "json":
                var10000 = MediaType.APPLICATION_JSON;
                break;
            case "xml":
                var10000 = MediaType.APPLICATION_XML;
                break;
            case "zip":
                var10000 = MediaType.valueOf("application/zip");
                break;
            case "mp4":
                var10000 = MediaType.valueOf("video/mp4");
                break;
            case "mp3":
                var10000 = MediaType.valueOf("audio/mpeg");
                break;
            case "doc":
            case "docx":
                var10000 = MediaType.valueOf("application/msword");
                break;
            case "xls":
            case "xlsx":
                var10000 = MediaType.valueOf("application/vnd.ms-excel");
                break;
            case "ppt":
            case "pptx":
                var10000 = MediaType.valueOf("application/vnd.ms-powerpoint");
                break;
            default:
                var10000 = MediaType.APPLICATION_OCTET_STREAM;
        }

        return var10000;
    }

    private boolean isImageFile(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean fileExists(String fileId) {
        try {
            FileInfo fileInfo = this.getFileInfo(fileId);
            return this.fileStorageService.fileExists(fileInfo.getFilepath());
        } catch (Exception var3) {
            log.debug("파일 존재 확인 실패: {}", fileId);
            return false;
        }
    }

    public ResponseEntity<Map<String, Object>> getFileMetadata(String fileId) {
        try {
            if (!FileIdUtils.isValid(fileId)) {
                return ResponseEntity.badRequest().build();
            } else {
                FileInfo fileInfo = this.getFileInfo(fileId);
                Map<String, Object> metadata = Map.of("fileId", fileInfo.getFileId(), "filename", fileInfo.getFilename(), "originalFilename", fileInfo.getOriginalFilename(), "mimeType", fileInfo.getMimeType() != null ? fileInfo.getMimeType() : "unknown", "fileSize", fileInfo.getFileSize(), "fileExtension", fileInfo.getFileExtension() != null ? fileInfo.getFileExtension() : "unknown", "uploadedAt", fileInfo.getUploadedAt(), "isImage", this.isImageFile(fileInfo.getMimeType()), "url", fileInfo.getUrl());
                return ResponseEntity.ok(metadata);
            }
        } catch (Exception e) {
            log.error("파일 메타데이터 조회 실패: {}", fileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    public void invalidateCache(String fileId) {
        this.fileCache.remove(fileId);
        log.debug("파일 캐시 무효화: {}", fileId);
    }

    public void clearAllCache() {
        int size = this.fileCache.size();
        this.fileCache.clear();
        log.info("파일 캐시 전체 삭제: {}개", size);
    }

    public Map<String, Object> getCacheStatus() {
        return Map.of("cacheSize", this.fileCache.size(), "cacheKeys", this.fileCache.keySet().size());
    }

    public ResponseEntity<Resource> getFileWithRange(String fileId, String rangeHeader) {
        try {
            if (!FileIdUtils.isValid(fileId)) {
                return ResponseEntity.badRequest().build();
            } else {
                FileInfo fileInfo = this.getFileInfo(fileId);
                Path path = this.fileStorageService.loadFile(fileInfo.getFilepath());
                long fileSize = Files.size(path);
                if (rangeHeader != null && !rangeHeader.isBlank()) {
                    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileSize - 1L;
                    if (start < fileSize && end < fileSize && start <= end) {
                        Resource resource = new UrlResource(path.toUri());
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
                        headers.add("Accept-Ranges", "bytes");
                        headers.setContentLength(end - start + 1L);
                        MediaType contentType = this.determineContentType(fileInfo);
                        return ((ResponseEntity.BodyBuilder) ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers)).contentType(contentType).body(resource);
                    } else {
                        return ((ResponseEntity.BodyBuilder) ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).header("Content-Range", new String[]{"bytes */" + fileSize})).build();
                    }
                } else {
                    return this.getFile(fileId, (String) null, (String) null, (String) null, (String) null);
                }
            }
        } catch (Exception e) {
            log.error("Range 요청 처리 실패: {}", fileId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Map<String, Object>> getImageInfo(String fileId) {
        try {
            if (!FileIdUtils.isValid(fileId)) {
                return ResponseEntity.badRequest().build();
            } else {
                FileInfo fileInfo = this.getFileInfo(fileId);
                if (!this.isImageFile(fileInfo.getMimeType())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일이 아닙니다."));
                } else {
                    Map<String, Object> imageInfo = Map.of("fileId", fileInfo.getFileId(), "originalFilename", fileInfo.getOriginalFilename(), "mimeType", fileInfo.getMimeType(), "fileSize", fileInfo.getFileSize(), "uploadedAt", fileInfo.getUploadedAt());
                    if (fileInfo instanceof ImageFileInfo) {
                        ImageFileInfo imgInfo = (ImageFileInfo) fileInfo;
                        Map<String, Object> extendedInfo = new HashMap(imageInfo);
                        extendedInfo.put("width", imgInfo.getOriginalWidth());
                        extendedInfo.put("height", imgInfo.getOriginalHeight());
                        extendedInfo.put("hasThumbnail", imgInfo.getHasThumbnail());
                        extendedInfo.put("quality", imgInfo.getQuality());
                        return ResponseEntity.ok(extendedInfo);
                    } else {
                        return ResponseEntity.ok(imageInfo);
                    }
                }
            }
        } catch (Exception e) {
            log.error("이미지 정보 조회 실패: {}", fileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    public EmbeddedFileAccessService(final FilePreStorageService fileStorageService, final EmbeddedFileUploadService<?> fileUploadService, final LoggingService loggingService, final BaseFileRepository<BaseFile, FileStatisticsDto> baseFileRepository) {
        this.fileStorageService = fileStorageService;
        this.fileUploadService = fileUploadService;
        this.loggingService = loggingService;
        this.baseFileRepository = baseFileRepository;
    }
}

