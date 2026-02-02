package com.ritsard.baisard.domain.inventory.controller;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.service.InventoryService;
import com.ritsard.baisard.file.controller.FileCrudable;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management API (ADMIN & SUPERADMIN)", description = "Manage inventory transactions")
//@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
public class InventoryController {
    private final InventoryService inventoryService;

    @PatchMapping("/products/{productId}/stock")
    @Operation(
            summary = "Update product stock quantity",
            description = "Directly update the stock quantity of a product"
    )
    public ApiResponse<?> stockProduct(
            @PathVariable UUID productId,
            @RequestParam BigDecimal quantity
    ) {
        inventoryService.stockProduct(productId, quantity);
        return ApiResponse.ok("Product stock updated successfully");
    }


    @PostMapping("/transactions")
    @Operation(
            summary = "Record inventory transaction",
            description = "Record stock IN, OUT, or ADJUSTMENT transaction"
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN', 'CASHIER')")
    public ApiResponse<?> recordTransaction(@Valid @RequestBody InventoryTransactionRequestDto request) {
        inventoryService.RecordInventoryTransaction(request);
        return ApiResponse.ok("Inventory transaction recorded successfully");
    }
}
