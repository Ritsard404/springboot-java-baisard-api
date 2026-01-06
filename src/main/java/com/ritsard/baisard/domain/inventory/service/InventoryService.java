package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.entity.Category;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.mapper.ProductMapper;
import com.ritsard.baisard.domain.inventory.repository.CategoryRepository;
import com.ritsard.baisard.domain.inventory.repository.InventoryRepository;
import com.ritsard.baisard.domain.inventory.repository.ProductRepository;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public Object getProducts(String keyword, String barcode, UUID uuidCategory, Integer page, Integer size, String sortBy, String direction) {
        return null;
    }

    @Override
    public void newProduct(ProductSaveDto productSaveDto) {

    }

    @Override
    public void editProduct(UUID uuidProduct, ProductSaveDto productSaveDto) {// Fetch category from DB first
        Category category = categoryRepository.findById(productSaveDto.getUuidCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

// For creation
        Product newProduct = ProductMapper.toEntity(productSaveDto, category);
        productRepository.save(newProduct);

    }

    @Override
    public void deleteProduct(UUID uuidProduct) {
        Product product = productRepository.findById(uuidProduct)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        product.softDelete();
        productRepository.save(product);

    }

    @Override
    public List<CategoryDto> getCategories() {
        return List.of();
    }

    @Override
    public void newCategory(CategoryDto categoryDto) {

    }

    @Override
    public void updateCategory(UUID uuidCategory, CategoryDto categoryDto) {

    }

    @Override
    public void deleteCategory(UUID uuidCategory) {
        Category category = categoryRepository.findById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.softDelete();
        categoryRepository.save(category);
    }

    @Override
    public void RecordInventoryTransaction(InventoryTransactionRequestDto dto) {

    }
}
