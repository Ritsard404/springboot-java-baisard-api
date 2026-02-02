package com.ritsard.baisard.domain.inventory.controller;

import com.ritsard.baisard.domain.inventory.service.InventoryService;
import com.ritsard.baisard.domain.inventory.service.ProductService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/inventory/files")
@RequiredArgsConstructor
@Tag(name = "Inventory Batch Upload API")
public class InventoryFileController {

    private final ProductService productService;

    @PostMapping(value = "/products/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload CSV file", description = "Upload a CSV file for batch product registration")
    public ApiResponse<?> uploadProducts(@RequestParam("file") MultipartFile file) {
        productService.batchUploadNewProducts(file);
        return ApiResponse.ok("File uploaded and processed successfully");
    }

    @GetMapping("/products/template")
    @Operation(summary = "Download CSV template", description = "Download the CSV template for batch upload")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] csvData = productService.generateCsvTemplate();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory_template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
