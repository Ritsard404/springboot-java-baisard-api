package com.ritsard.baisard.domain.inventory.controller;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.service.IInventoryService;
import com.ritsard.baisard.file.controller.FileCrudable;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management API", description = "Manage products, categories, and inventory transactions")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
public class InventoryController implements FileCrudable<Product, ImageFileInfo> {
    private final IInventoryService inventoryService;// ==================== PRODUCT ENDPOINTS ====================

    @GetMapping("/products")
    @Operation(summary = "Get product list", description = "Retrieve paginated and filtered product list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Object products = inventoryService.getProducts(keyword, barcode, categoryId, page, size, sortBy, direction);
        return ApiResponse.ok(products);
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get product detail", description = "Retrieve single product information")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<ProductDto> getProduct(@PathVariable UUID productId) {
        ProductDto product = inventoryService.getProduct(productId);
        return ApiResponse.ok(product);
    }

    @PostMapping("/products")
    @Operation(summary = "Create new product", description = "Add a new product to inventory")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> createProduct(@Valid @RequestBody ProductSaveDto request) {
        inventoryService.newProduct(request);
        return ApiResponse.ok("Product created successfully");
    }

    @PatchMapping("/products/{productId}/stock")
    @Operation(
            summary = "Update product stock quantity",
            description = "Directly update the stock quantity of a product"
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> stockProduct(
            @PathVariable UUID productId,
            @RequestParam BigDecimal quantity
    ) {
        inventoryService.stockProduct(productId, quantity);
        return ApiResponse.ok("Product stock updated successfully");
    }

    @PutMapping("/products/{productId}")
    @Operation(summary = "Update product", description = "Update existing product information")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductSaveDto request
    ) {
        inventoryService.editProduct(productId, request);
        return ApiResponse.ok("Product updated successfully");
    }

    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Delete product", description = "Soft delete a product from inventory")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> deleteProduct(@PathVariable UUID productId) {
        inventoryService.deleteProduct(productId);
        return ApiResponse.ok("Product deleted successfully");
    }

    // ==================== CATEGORY ENDPOINTS ====================

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieve list of all product categories")
    public ApiResponse<List<CategoryDto>> getCategories() {
        List<CategoryDto> categories = inventoryService.getCategories();
        return ApiResponse.ok(categories);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Get category detail", description = "Retrieve single category information")
    public ApiResponse<CategoryDto> getCategory(@PathVariable UUID categoryId) {
        CategoryDto category = inventoryService.getCategory(categoryId);
        return ApiResponse.ok(category);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create new category", description = "Add a new product category")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> createCategory(@Valid @RequestBody CategoryDto request) {
        inventoryService.newCategory(request);
        return ApiResponse.ok("Category created successfully");
    }

    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "Update category", description = "Update existing category information")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryDto request
    ) {
        inventoryService.updateCategory(categoryId, request);
        return ApiResponse.ok("Category updated successfully");
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Delete category", description = "Soft delete a category (only if no products exist)")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> deleteCategory(@PathVariable UUID categoryId) {
        inventoryService.deleteCategory(categoryId);
        return ApiResponse.ok("Category deleted successfully");
    }

    // ==================== INVENTORY TRANSACTION ENDPOINTS ====================

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
