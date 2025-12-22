package com.ritsard.baisard.file.controller;

import com.ritsard.baisard.file.service.v4.EmbeddedFileAccessService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping(value = {"/files"})
public class UnifiedFileController {
    private final EmbeddedFileAccessService fileAccessService;

    @GetMapping(value = {"/{encryptedId}"})
    @Operation(summary = "View or Download File")
    public ResponseEntity<Resource> accessFile(
            @PathVariable String encryptedId,
            @RequestParam(defaultValue = "inline") String disposition,
            @RequestParam(defaultValue = "original") String type,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince) {
        return this.fileAccessService.getFile(encryptedId, disposition, type, ifNoneMatch, ifModifiedSince);
    }

    public UnifiedFileController(EmbeddedFileAccessService fileAccessService) {
        this.fileAccessService = fileAccessService;
    }
}