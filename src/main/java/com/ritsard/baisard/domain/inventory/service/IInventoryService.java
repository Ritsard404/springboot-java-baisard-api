package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Category;

import java.util.List;
import java.util.UUID;

public interface IInventoryService {
    //    Product Services
    public Object getProducts(String keyword, String barcode, UUID uuidCategory,
                              Integer page, Integer size, String sortBy, String direction);

    public ProductDto getProduct(UUID uuidProduct);

    public void newProduct(ProductSaveDto productSaveDto);

    public void editProduct(UUID uuidProduct, ProductSaveDto productSaveDto);

    public void deleteProduct(UUID uuidProduct);

    // Category Services?
    public List<CategoryDto> getCategories();

    public CategoryDto getCategory(UUID uuidCategory);

    public void newCategory(CategoryDto categoryDto);

    public void updateCategory(UUID uuidCategory, CategoryDto categoryDto);

    public void deleteCategory(UUID uuidCategory);

    // Inventory Record
    public void RecordInventoryTransaction(InventoryTransactionRequestDto dto);
}
