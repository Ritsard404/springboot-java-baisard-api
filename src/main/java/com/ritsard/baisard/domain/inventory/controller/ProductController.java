package com.ritsard.baisard.domain.inventory.controller;

import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.service.ProductService;
import com.ritsard.baisard.file.controller.FileCrudable;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management API (ADMIN & SUPERADMIN)", description = "Manage products, categories, and inventory transactions")
//@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
public class ProductController implements FileCrudable<Product, ImageFileInfo> {
    private final ProductService productService;
    // ==================== PRODUCT ENDPOINTS ====================

    @GetMapping()
    @Operation(summary = "Get product list", description = "Retrieve paginated and filtered product list")
    public ApiResponse<?> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Object products = productService.getProducts(keyword, barcode, categoryId, page, size, sortBy, direction);
        return ApiResponse.ok(products);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Get product list by category", description = "Retrieve paginated and filtered product by category list")
    public ApiResponse<?> getProductsByCategory(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Object products = productService.getProductsByCategory(categoryId, page, size, sortBy, direction);
        return ApiResponse.ok(products);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product detail", description = "Retrieve single product information")
    public ApiResponse<ProductDto> getProduct(@PathVariable UUID productId) {
        ProductDto product = productService.getProduct(productId);
        return ApiResponse.ok(product);
    }

    @PostMapping()
    @Operation(summary = "Create new product", description = "Add a new product to inventory")
    public ApiResponse<?> createProduct(@Valid @RequestBody ProductSaveDto request) {
        productService.newProduct(request);
        return ApiResponse.ok("Product created successfully");
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Update existing product information")
    public ApiResponse<?> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductSaveDto request
    ) {
        productService.editProduct(productId, request);
        return ApiResponse.ok("Product updated successfully");
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Soft delete a product from inventory")
    public ApiResponse<?> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ApiResponse.ok("Product deleted successfully");
    }

    // ==================== BATCH OPERATIONS ====================

    @PostMapping("/batch")
    @Operation(summary = "Batch upload products", description = "Upload a list of products directly via JSON")
    public ApiResponse<?> batchCreateProducts(@Valid @RequestBody List<ProductSaveDto> dtos) {
        productService.newProducts(dtos);
        return ApiResponse.ok("Batch products created successfully");
    }
}
