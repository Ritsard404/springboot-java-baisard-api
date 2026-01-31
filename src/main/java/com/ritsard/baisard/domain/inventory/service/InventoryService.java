package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InventoryService {
    //    Product Services
    Object getProducts(String keyword, String barcode, UUID uuidCategory,
                       Integer page, Integer size, String sortBy, String direction);

    ProductDto getProduct(UUID uuidProduct);

    Slice<ProductDto> getProductsByCategory(UUID uuidCategory, Integer page, Integer size, String sortBy, String direction);

    void stockProduct(UUID uuidProduct, BigDecimal qty);

    void newProduct(ProductSaveDto productSaveDto);

    void newProducts(List<ProductSaveDto> dtos);

    void batchUploadNewProducts(MultipartFile file);

    public byte[] generateCsvTemplate();

    void editProduct(UUID uuidProduct, ProductSaveDto productSaveDto);

    void deleteProduct(UUID uuidProduct);

    // Category Services?
    List<CategoryDto> getCategories();

    CategoryDto getCategory(UUID uuidCategory);

    void newCategory(CategoryDto categoryDto);

    void updateCategory(UUID uuidCategory, CategoryDto categoryDto);

    void deleteCategory(UUID uuidCategory);

    // Inventory Record
    void RecordInventoryTransaction(InventoryTransactionRequestDto dto);
}
