package com.ritsard.baisard.file.controller;

import com.ritsard.baisard.file.dto.v3.FileUploadRequest;
import com.ritsard.baisard.file.dto.v3.FileUploadResponse;
import com.ritsard.baisard.file.entity.v2.FileInfo;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.file.service.v3.SpringContext;
import com.ritsard.baisard.file.service.v4.EmbeddedFileAccessService;
import com.ritsard.baisard.file.service.v4.EmbeddedFileUploadService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface FileCrudable<T, F extends FileInfo> {
    default Class<T> getEntityClass() {
        Type[] interfaces = this.getClass().getGenericInterfaces();

        for (Type type : interfaces) {
            if (type instanceof ParameterizedType paramType) {
                Type rawType = paramType.getRawType();
                if (rawType.equals(FileCrudable.class)) {
                    Type[] typeArguments = paramType.getActualTypeArguments();
                    if (typeArguments.length > 0) {
                        return (Class) typeArguments[0];
                    }
                }
            }
        }

        throw new IllegalStateException("Generic type not found.");
    }

    default Class<F> getFileInfoClass() {
        Type[] interfaces = this.getClass().getGenericInterfaces();

        for (Type type : interfaces) {
            if (type instanceof ParameterizedType paramType) {
                Type rawType = paramType.getRawType();
                if (rawType.equals(FileCrudable.class)) {
                    Type[] typeArguments = paramType.getActualTypeArguments();
                    if (typeArguments.length > 1) {
                        return (Class<F>) typeArguments[1]; // Safe cast
                    }
                }
            }
        }

        return (Class<F>) ImageFileInfo.class; // Cast default type
    }

    default String getEntityTypeName() {
        return this.getEntityClass().getSimpleName().toLowerCase();
    }

    default EmbeddedFileUploadService<F> getFileService() {
        EmbeddedFileUploadService<F> service = (EmbeddedFileUploadService) SpringContext.getBean(EmbeddedFileUploadService.class);
        service.setFileInfoClass(this.getFileInfoClass());
        return service;
    }

    @PostMapping(
            value = {"/files/upload"},
            consumes = {"multipart/form-data"}
    )
    @Operation(
            summary = "File Upload",
            description = "Uploads a file and stores it temporarily."
    )
    default ApiResponse<FileUploadResponse> uploadFile(
            @RequestPart("file") @Parameter(description = "File to upload", required = true) MultipartFile file,
            @RequestPart(value = "metadata", required = false) @Parameter(description = "File metadata") FileUploadRequest metadata) {
        FileUploadResponse response = this.getFileService().uploadFile(file, metadata);
        return ApiResponse.ok(response);
    }

    @PostMapping(
            value = {"/files/upload/multiple"},
            consumes = {"multipart/form-data"}
    )
    @Operation(
            summary = "Multiple File Upload",
            description = "Uploads multiple files at once."
    )
    default ApiResponse<List<FileUploadResponse>> uploadMultipleFiles(
            @Parameter(description = "Files to upload", required = true) @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "File metadata") @RequestPart(value = "metadata", required = false) FileUploadRequest metadata) {
        List<FileUploadResponse> responses = this.getFileService().uploadMultipleFiles(files, metadata);
        return ApiResponse.ok(responses);
    }

    @PostMapping({"/files/attach"})
    @Operation(
            summary = "Attach Files",
            description = "Connects uploaded files to an entity."
    )
    default ApiResponse<Void> attachFiles(
            @Parameter(description = "List of file IDs to connect", required = true) @RequestBody List<String> encryptedFileIds,
            @Parameter(description = "ID of the entity to connect", required = true) @RequestParam UUID entityId) {
        return ApiResponse.ok();
    }

    @DeleteMapping({"/files/{encryptedId}"})
    @Operation(
            summary = "Delete File",
            description = "Deletes a file."
    )
    default ApiResponse<Void> deleteFile(
            @Parameter(description = "Encrypted file ID", required = true) @PathVariable String encryptedId) {
        this.getFileService().deleteFile(encryptedId);
        return ApiResponse.ok();
    }

    @GetMapping({"/files/{encryptedId}/info"})
    @Operation(
            summary = "Get File Information",
            description = "Retrieves file metadata."
    )
    default ApiResponse<FileUploadResponse> getFileInfo(
            @Parameter(description = "Encrypted file ID", required = true) @PathVariable String encryptedId) {
        FileUploadResponse fileInfo = this.getFileService().getFileInfo(encryptedId);
        return ApiResponse.ok(fileInfo);
    }

    @GetMapping({"/files/{encryptedId}"})
    @Operation(
            summary = "Access or Download File",
            description = "Views or downloads a file."
    )
    default ResponseEntity<Resource> accessFile(
            @Parameter(description = "Encrypted file ID", required = true) @PathVariable String encryptedId,
            @Parameter(description = "Processing method", example = "inline") @RequestParam(defaultValue = "inline") String disposition,
            @Parameter(description = "File type", example = "original") @RequestParam(defaultValue = "original") String type,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince) {
        EmbeddedFileAccessService service = (EmbeddedFileAccessService) SpringContext.getBean(EmbeddedFileAccessService.class);
        return service.getFile(encryptedId, disposition, type, ifNoneMatch, ifModifiedSince);
    }

    @GetMapping({"/files/list/{entityId}"})
    @Operation(
            summary = "Get Entity File List",
            description = "Retrieves the list of files connected to a specific entity."
    )
    default ApiResponse<List<FileUploadResponse>> getFilesByEntity(
            @Parameter(description = "Entity ID", required = true) @PathVariable UUID entityId) {
        return ApiResponse.ok(List.of());
    }
}