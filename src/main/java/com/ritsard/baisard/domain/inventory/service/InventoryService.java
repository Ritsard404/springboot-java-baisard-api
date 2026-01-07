package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Category;
import com.ritsard.baisard.domain.inventory.entity.Inventory;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.mapper.ProductMapper;
import com.ritsard.baisard.domain.inventory.repository.CategoryRepository;
import com.ritsard.baisard.domain.inventory.repository.InventoryRepository;
import com.ritsard.baisard.domain.inventory.repository.ProductRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.global.utils.Formats;
import com.ritsard.baisard.global.utils.ImageUtils;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import com.ritsard.baisard.utils.exceptions.files.InvalidFileTypeException;
import com.ritsard.baisard.utils.helper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ImageUtils imageUtils;

    @Override
    public Object getProducts(String keyword, String barcode, UUID uuidCategory, Integer page, Integer size, String sortBy, String direction) {
        // Default paging & sorting
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Sort sort = Sort.by(sortBy != null ? sortBy : "name");
        sort = "desc".equalsIgnoreCase(direction) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Normalize keyword to lowercase for JPQL
        String keywordFilter = keyword != null ? keyword.toLowerCase() : null;

        Page<Product> products = productRepository.findProductsWithConditions(
                keywordFilter,
                barcode,
                uuidCategory,
                pageable
        );

        // Map to DTO
        AtomicInteger counter = new AtomicInteger(pageNumber * pageSize + 1);

        return PageHelper.toPageResponse(
                products,
                product -> ProductDto.builder()
                        .uuidProduct(product.getUuidProduct())
                        .name(product.getName())
                        .productImageUrl(product.getProductImageUrl())
                        .barcode(product.getBarcode())
                        .baseUnit(product.getBaseUnit())
                        .quantity(product.getQuantity())
                        .cost(product.getCost())
                        .price(product.getPrice())
                        .isAvailable(product.getIsAvailable())
                        .itemType(product.getItemType())
                        .vatType(product.getVatType())
                        .categoryId(product.getCategory() != null ? product.getCategory().getUuidCategory() : null)
                        .build()
        );
    }

    @Override
    public ProductDto getProduct(UUID uuidProduct) {

        Product existProduct = productRepository.findById(uuidProduct).
                orElseThrow(() -> new NotFoundException("Product not found."));
        return ProductDto.builder()
                .uuidProduct(existProduct.getUuidProduct())
                .name(existProduct.getName())
                .productImageUrl(existProduct.getProductImageUrl())
                .barcode(existProduct.getBarcode())
                .baseUnit(existProduct.getBaseUnit())
                .quantity(existProduct.getQuantity())
                .cost(existProduct.getCost())
                .price(existProduct.getPrice())
                .isAvailable(existProduct.getIsAvailable())
                .itemType(existProduct.getItemType())
                .vatType(existProduct.getVatType())
                .categoryId(existProduct.getCategory() != null ? existProduct.getCategory().getUuidCategory() : null)
                .build();
    }

    @Override
    public void stockProduct(UUID uuidProduct, BigDecimal qty) {

        if (qty == null || qty.compareTo(BigDecimal.ZERO) == 0)
            throw new ConflictException("Quantity must not be zero.");

        if (!productRepository.existsById(uuidProduct))
            throw new NotFoundException("Product not found.");

        productRepository.incrementStock(uuidProduct, qty);

//                auditLog.addManagerAudit(
//                        String.format("Stock IN: %s units added to product '%s' (Ref: %s)",
//                                qty, product.getName(), dto.getReference())
//                );
    }

    @Override
    public void newProduct(ProductSaveDto productSaveDto) {
        if (productRepository.existsByNameIgnoreCaseAndCategory_UuidCategory(
                productSaveDto.getName(),
                productSaveDto.getUuidCategory()
        )) {
            throw new ConflictException("Product name already exists in this category");
        }

        Category category = categoryRepository.findById(productSaveDto.getUuidCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = ProductMapper.toEntity(productSaveDto, category);

        List<String> fileIds = new ArrayList<>();
        if (productSaveDto.getEncryptedProductImageId() != null) {
            fileIds.add(productSaveDto.getEncryptedProductImageId());
        }

        if (!fileIds.isEmpty()) {
            try {
                product.setFileIds(fileIds);
            } catch (IOException e) {
                throw new RuntimeException("Error setting product file IDs: " + e.getMessage(), e);
            }

            imageUtils.setImageUrlFromFiles(
                    productSaveDto.getEncryptedProductImageId(),
                    product.getFileList(),
                    product::setProductImageUrl
            );
        }

        productRepository.save(product);
    }

    @Override
    public void editProduct(UUID uuidProduct, ProductSaveDto productSaveDto) {
        if (productRepository.existsByNameIgnoreCaseAndCategory_UuidCategoryAndUuidProductNot(
                productSaveDto.getName(),
                productSaveDto.getUuidCategory(),
                uuidProduct
        )) {
            throw new ConflictException("Product name already exists in this category");
        }
        // Fetch category from DB first
        Category category = categoryRepository.findById(productSaveDto.getUuidCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product existProduct = productRepository.findById(uuidProduct).
                orElseThrow(() -> new NotFoundException("Product not found."));

        ProductMapper.updateEntity(existProduct, productSaveDto, category);

        if (productSaveDto.getEncryptedProductImageId() != null) {
            List<String> fileIds = List.of(productSaveDto.getEncryptedProductImageId());

            try {
                existProduct.setFileIds(fileIds); // replaces old image
            } catch (IOException e) {
                throw new RuntimeException("Error setting product file IDs", e);
            }

            imageUtils.setImageUrlFromFiles(
                    productSaveDto.getEncryptedProductImageId(),
                    existProduct.getFileList(),
                    existProduct::setProductImageUrl
            );
        }
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
        return categoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(c -> new CategoryDto(c.getCategoryName()))
                .toList();
    }

    @Override
    public CategoryDto getCategory(UUID uuidCategory) {

        Category category = categoryRepository.findById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return new CategoryDto(category.getCategoryName());
    }

    @Override
    public void newCategory(CategoryDto categoryDto) {

        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryDto.getCategoryName())) {
            throw new ConflictException("Category already exists");
        }

        Category newCategory = Category.builder()
                .categoryName(Formats.capitalize(categoryDto.getCategoryName()))
                .build();
        categoryRepository.save(newCategory);

    }

    @Override
    public void updateCategory(UUID uuidCategory, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.setCategoryName(Formats.capitalize(categoryDto.getCategoryName()));
    }

    @Override
    public void deleteCategory(UUID uuidCategory) {
        if (categoryRepository.existsByUuidCategoryAndIsDeletedFalse(uuidCategory)) {
            throw new ConflictException("Cannot delete category with existing products");
        }

        Category category = categoryRepository.findById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.softDelete();
        categoryRepository.save(category);
    }

    @Override
    public void RecordInventoryTransaction(InventoryTransactionRequestDto dto) {

        // Validate transaction type
        if (dto.getInventoryTransactionType() == null)
            throw new ConflictException("Inventory transaction type is required.");


        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
            throw new ConflictException("Quantity must be greater than zero.");


        // Fetch product
        Product product = productRepository.findById(dto.getUuidProduct())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        BigDecimal qty = dto.getQuantity();

        // Adjust product quantity
        switch (dto.getInventoryTransactionType()) {
            case IN -> {
                product.setQuantity(product.getQuantity() != null
                        ? product.getQuantity().add(qty)
                        : qty);

//                auditLog.addManagerAudit(
//                        String.format("Stock IN: %s units added to product '%s' (Ref: %s)",
//                                qty, product.getName(), dto.getReference())
//                );
            }
            case OUT -> {
                if (product.getQuantity() == null || product.getQuantity().compareTo(qty) < 0) {
                    throw new IllegalStateException("Not enough stock.");
                }
                product.setQuantity(product.getQuantity().subtract(qty));

//                auditLog.addCashierAudit(
//                        String.format("Stock OUT: %s units removed from product '%s' (Ref: %s)",
//                                qty, product.getName(), dto.getReference())
//                );
            }
            case ADJUSTMENT -> {
                product.setQuantity(qty);

//                auditLog.addManagerAudit(
//                        String.format("Stock ADJUSTMENT: Product '%s' quantity set to %s (Ref: %s)",
//                                product.getName(), qty, dto.getReference())
//                );
            }
            default -> throw new IllegalArgumentException("Invalid inventory transaction type.");
        }

        // Create inventory record
        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(qty)
                .type(dto.getInventoryTransactionType())
                .reference(dto.getReference())
                .build();

        inventoryRepository.save(inventory);
        productRepository.save(product);
    }

}
