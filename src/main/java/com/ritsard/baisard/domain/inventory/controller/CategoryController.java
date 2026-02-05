package com.ritsard.baisard.domain.inventory.controller;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.service.CategoryService;
import com.ritsard.baisard.domain.inventory.service.InventoryService;
import com.ritsard.baisard.file.controller.FileCrudable;
import com.ritsard.baisard.file.entity.v2.ImageFileInfo;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management API (ADMIN & SUPERADMIN)", description = "Manage categories")
//@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    @Operation(summary = "Get all company categories detail", description = "Retrieve categories information")
    public ApiResponse<?> getCategories() {
        List<CategoryDto> category = categoryService.getCategories();
        return ApiResponse.ok(category);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category detail", description = "Retrieve single category information")
    public ApiResponse<CategoryDto> getCategory(@PathVariable UUID categoryId) {
        CategoryDto category = categoryService.getCategory(categoryId);
        return ApiResponse.ok(category);
    }

    @PostMapping()
    @Operation(summary = "Create new category", description = "Add a new product category")
    public ApiResponse<?> createCategory(@Valid @RequestBody CategoryDto request) {
        categoryService.newCategory(request);
        return ApiResponse.ok("Category created successfully");
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update existing category information")
    public ApiResponse<?> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryDto request
    ) {
        categoryService.updateCategory(categoryId, request);
        return ApiResponse.ok("Category updated successfully");
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Soft delete a category (only if no products exist)")
    public ApiResponse<?> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.ok("Category deleted successfully");
    }
}
