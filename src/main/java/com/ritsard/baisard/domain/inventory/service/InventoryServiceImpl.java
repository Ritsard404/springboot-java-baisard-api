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
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.global.exception.InventoryException;
import com.ritsard.baisard.global.utils.Formats;
import com.ritsard.baisard.global.utils.ImageUtils;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import com.ritsard.baisard.utils.helper.PageHelper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ImageUtils imageUtils;
    private final AuthManager<Member> authManager;
    private final Validator validator;

    @Override
    public Object getProducts(String keyword, String barcode, UUID uuidCategory, Integer page, Integer size, String sortBy, String direction) {
        Member member = authManager.getMember();
        // Safely get the Company UUID. If member or company is null, companyUuid becomes null.
        UUID companyUuid = (member != null && member.getCompany() != null)
                ? member.getCompany().getUuidCompany()
                : null;

        // Default paging & sorting
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Sort sort = Sort.by(sortBy != null ? sortBy : "name");
        sort = "desc".equalsIgnoreCase(direction) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Normalize keyword to lowercase for JPQL
        String keywordFilter = keyword != null ? keyword.toLowerCase() : null;

        Page<ProductDto> products =
                productRepository.findProductsWithConditions(
                        keywordFilter,
                        barcode,
                        uuidCategory,
                        companyUuid,
                        pageable
                );

        return PageHelper.toPageResponse(products, Function.identity());
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
    public Slice<ProductDto> getProductsByCategory(UUID uuidCategory, Integer page, Integer size, String sortBy, String direction) {
        Member member = authManager.getMember();
        UUID companyUuid = (member != null && member.getCompany() != null)
                ? member.getCompany().getUuidCompany()
                : null;
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Sort sort = Sort.by(sortBy != null ? sortBy : "name");
        sort = "desc".equalsIgnoreCase(direction) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);


        return productRepository.findProductsByCategoryForPOS(
                uuidCategory,
                companyUuid,
                pageable
        );
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
        Member member = authManager.getMember();
        Company company = member.getCompany(); // Assuming Member has getCompany() method

        // Find or create category (scoped to company)
        Category category = Optional.ofNullable(productSaveDto.getUuidCategory())
                .flatMap(categoryRepository::findById)
                .filter(cat -> cat.getCompany().getUuidCompany().equals(company.getUuidCompany()))
                .or(() -> categoryRepository.findByCategoryNameIgnoreCaseAndCompany_UuidCompany(
                        productSaveDto.getCategoryName(),
                        company.getUuidCompany()
                ))
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .categoryName(productSaveDto.getCategoryName().trim().toUpperCase())
                                .company(company)
                                .build()
                ));

        if (productRepository.existsByNameIgnoreCaseAndCategory_UuidCategory(
                productSaveDto.getName(),
                productSaveDto.getUuidCategory()
        )) {
            throw new ConflictException("Product name already exists in this category");
        }


        Product product = ProductMapper.toEntity(productSaveDto, category, member.getCompany());

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
    public void newProducts(List<ProductSaveDto> dtos) {
        Member member = authManager.getMember();
        Company company = member.getCompany();

        // 1. Cache categories (Case-Insensitive)
        Map<String, Category> categoryCache = categoryRepository.findByCompany_UuidCompany(company.getUuidCompany())
                .stream()
                .collect(Collectors.toMap(c -> c.getCategoryName().toUpperCase(), c -> c));

        List<Product> productsToSave = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            ProductSaveDto dto = dtos.get(i);

            // 2. Manual Validation Check
            var violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String error = violations.iterator().next().getMessage();
                throw new ConflictException("Row " + (i + 1) + " is invalid: " + error);
            }

            // 3. Optimized Category Handling
            String catName = dto.getCategoryName().trim().toUpperCase();
            Category category = categoryCache.get(catName);

            if (category == null) {
                category = categoryRepository.save(Category.builder()
                        .categoryName(catName)
                        .company(company)
                        .build());
                categoryCache.put(catName, category);
            }

            // 4. Duplicate Check
            if (productRepository.existsByNameIgnoreCaseAndCategory_UuidCategory(dto.getName(), category.getUuidCategory())) {
                throw new ConflictException("Product '" + dto.getName() + "' already exists in " + catName);
            }

            // 5. Mapping & Image Logic
            Product product = ProductMapper.toEntity(dto, category, company);
            handleImageInBulk(product, dto.getEncryptedProductImageId());

            productsToSave.add(product);
        }

        // 6. Batch Save (Performance win)
        productRepository.saveAll(productsToSave);
    }

    private void handleImageInBulk(Product product, String imageId) {
        if (imageId == null || imageId.isBlank()) return;
        try {
            product.setFileIds(Collections.singletonList(imageId));
            imageUtils.setImageUrlFromFiles(imageId, product.getFileList(), product::setProductImageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Image error: " + e.getMessage());
        }
    }

    @Override
    public void batchUploadNewProducts(MultipartFile file) {
        if (file.isEmpty())
            throw new InventoryException("Uploaded file is empty");

        List<ProductSaveDto> dtos = parse(file);
        newProducts(dtos);
    }

    @Override
    public byte[] generateCsvTemplate() {
        String header = "Product Name,Category Name,Price,Cost,Quantity,Barcode,Base Unit\n";
        String sample = "Sample Item,Category A,100.00,80.00,5,987654321,UNIT\n";
        return (header + sample).getBytes(StandardCharsets.UTF_8);
    }

    private List<ProductSaveDto> parse(MultipartFile file) {

        String filename = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();

        try {
            if (filename.endsWith(".csv")) {
                return parseCsv(file);
            }


        } catch (IOException e) {
            throw new InventoryException("Failed to read file");
        }

        throw new InventoryException("Only CSV  files are supported");
    }

    private List<ProductSaveDto> parseCsv(MultipartFile file) throws IOException {
        List<ProductSaveDto> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Using -1 to keep empty trailing columns
                String[] columns = line.split(",", -1);

                if (columns.length < 4) continue; // Basic structure check

                result.add(ProductSaveDto.builder()
                        .name(columns[0].trim())
                        .categoryName(columns[1].trim())
                        .price(parseBigDecimal(columns[2]))
                        .quantity(parseBigDecimal(columns[3]))
                        .cost(columns.length > 4 ? parseBigDecimal(columns[4]) : BigDecimal.ZERO)
                        .baseUnit(columns.length > 5 ? columns[5].trim() : "UNIT")
                        .build());
            }
        }
        return result;
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return (value == null || value.isBlank()) ? BigDecimal.ZERO : new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public void editProduct(UUID uuidProduct, ProductSaveDto productSaveDto) {

        Product existProduct = productRepository.findById(uuidProduct)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Find or create category

        Category category = Optional.ofNullable(productSaveDto.getUuidCategory())
                .flatMap(categoryRepository::findById)
                .filter(cat -> cat.getCompany().getUuidCompany().equals(memberCompany())) // Ensure category belongs to company
                .or(() -> categoryRepository.findByCategoryNameIgnoreCaseAndCompany_UuidCompany(
                        productSaveDto.getCategoryName(),
                        memberCompany().getUuidCompany()
                ))
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .categoryName(productSaveDto.getCategoryName().trim().toUpperCase())
                                .company(memberCompany())
                                .build()
                ));

        // Optional: keep category name in sync
        if (!category.getCategoryName().equals(productSaveDto.getCategoryName())) {
            category.setCategoryName(productSaveDto.getCategoryName());
        }

        // Prevent duplicate product name
        if (productRepository.existsByNameIgnoreCaseAndCategory_UuidCategoryAndUuidProductNot(
                productSaveDto.getName(),
                category.getUuidCategory(),
                uuidProduct
        )) {
            throw new ConflictException("Product name already exists in this category");
        }

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
        Member member = authManager.getMember();
        UUID companyUuid = (member != null && member.getCompany() != null)
                ? member.getCompany().getUuidCompany()
                : null;
        return categoryRepository.findAllDto(companyUuid);
    }

    @Override
    public CategoryDto getCategory(UUID uuidCategory) {
        return categoryRepository.findDtoById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public void newCategory(CategoryDto dto) {
        String name = dto.getCategoryName().trim(); // Crucial: removes hidden spaces

        if (categoryRepository.existsByCategoryNameIgnoreCaseAndCompany_UuidCompany(name, memberCompany().getUuidCompany())) {
            throw new ConflictException("Category '" + name + "' already exists in your company.");
        }

        categoryRepository.save(Category.builder()
                .categoryName(name)
                .company(memberCompany())
                .build());
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

    private Company memberCompany() {
        Member member = authManager.getMember();
        return member.getCompany();
    }
}
