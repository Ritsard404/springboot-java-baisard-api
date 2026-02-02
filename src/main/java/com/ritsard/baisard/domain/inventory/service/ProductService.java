package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Object getProducts(
            String keyword,
            String barcode,
            UUID uuidCategory,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    );

    ProductDto getProduct(UUID uuidProduct);

    Slice<ProductDto> getProductsByCategory(
            UUID uuidCategory,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    );

    void newProduct(ProductSaveDto productSaveDto);

    void newProducts(List<ProductSaveDto> dtos);

    void editProduct(UUID uuidProduct, ProductSaveDto productSaveDto);

    void deleteProduct(UUID uuidProduct);

    // Imports / Templates
    void batchUploadNewProducts(MultipartFile file);

    byte[] generateCsvTemplate();
}
