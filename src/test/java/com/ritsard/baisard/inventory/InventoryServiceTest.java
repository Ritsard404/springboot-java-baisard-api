package com.ritsard.baisard.inventory;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Category;
import com.ritsard.baisard.domain.inventory.entity.Inventory;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.enums.InventoryTransactionType;
import com.ritsard.baisard.domain.inventory.repository.CategoryRepository;
import com.ritsard.baisard.domain.inventory.repository.InventoryRepository;
import com.ritsard.baisard.domain.inventory.repository.ProductRepository;
import com.ritsard.baisard.domain.inventory.service.InventoryService;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.global.utils.ImageUtils;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Service Tests")
public class InventoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ImageUtils imageUtils;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID testUuid;
    private UUID categoryUuid;
    private Category testCategory;
    private Product testProduct;
    private ProductSaveDto productSaveDto;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        categoryUuid = UUID.randomUUID();

        testCategory = Category.builder()
                .uuidCategory(categoryUuid)
                .categoryName("Electronics")
                .build();

        testProduct = Product.builder()
                .uuidProduct(testUuid)
                .name("Test Product")
                .barcode("123456789")
                .baseUnit("pcs")
                .quantity(BigDecimal.valueOf(100))
                .cost(BigDecimal.valueOf(50.00))
                .price(BigDecimal.valueOf(75.00))
                .isAvailable(true)
                .category(testCategory)
                .build();

        productSaveDto = ProductSaveDto.builder()
                .name("Test Product")
                .barcode("123456789")
                .baseUnit("pcs")
                .cost(BigDecimal.valueOf(50.00))
                .price(BigDecimal.valueOf(75.00))
                .uuidCategory(categoryUuid)
                .build();
    }

    @Nested
    @DisplayName("Product Tests")
    class ProductTests {

        @Test
        @DisplayName("Should get products with pagination")
        void shouldGetProductsWithPagination() {
            // Arrange
            List<Product> products = List.of(testProduct);
            Page<Product> productPage = new PageImpl<>(products);

            when(productRepository.findProductsWithConditions(
                    any(), any(), any(), any(Pageable.class)))
                    .thenReturn(productPage);

            // Act
            Object result = inventoryService.getProducts(
                    "test", null, null, 0, 10, "name", "asc");

            // Assert
            assertNotNull(result);
            verify(productRepository).findProductsWithConditions(
                    eq("test"), isNull(), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should get single product by UUID")
        void shouldGetProductByUuid() {
            // Arrange
            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));

            // Act
            ProductDto result = inventoryService.getProduct(testUuid);

            // Assert
            assertNotNull(result);
            assertEquals(testUuid, result.getUuidProduct());
            assertEquals("Test Product", result.getName());
            assertEquals("123456789", result.getBarcode());
            verify(productRepository).findById(testUuid);
        }

        @Test
        @DisplayName("Should throw NotFoundException when product not found")
        void shouldThrowNotFoundExceptionWhenProductNotFound() {
            // Arrange
            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class,
                    () -> inventoryService.getProduct(testUuid));
            verify(productRepository).findById(testUuid);
        }

        @Test
        @DisplayName("Should create new product successfully")
        void shouldCreateNewProduct() {
            // Arrange
            when(productRepository.existsByNameIgnoreCaseAndCategory_UuidCategory(
                    anyString(), any(UUID.class))).thenReturn(false);
            when(categoryRepository.findById(categoryUuid))
                    .thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class)))
                    .thenReturn(testProduct);

            // Act
            inventoryService.newProduct(productSaveDto);

            // Assert
            verify(productRepository).existsByNameIgnoreCaseAndCategory_UuidCategory(
                    productSaveDto.getName(), categoryUuid);
            verify(categoryRepository).findById(categoryUuid);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ConflictException when product name already exists")
        void shouldThrowConflictExceptionWhenProductNameExists() {
            // Arrange
            when(productRepository.existsByNameIgnoreCaseAndCategory_UuidCategory(
                    anyString(), any(UUID.class))).thenReturn(true);

            // Act & Assert
            assertThrows(ConflictException.class,
                    () -> inventoryService.newProduct(productSaveDto));
            verify(productRepository).existsByNameIgnoreCaseAndCategory_UuidCategory(
                    productSaveDto.getName(), categoryUuid);
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should edit product successfully")
        void shouldEditProduct() {
            // Arrange
            when(productRepository.existsByNameIgnoreCaseAndCategory_UuidCategoryAndUuidProductNot(
                    anyString(), any(UUID.class), any(UUID.class))).thenReturn(false);
            when(categoryRepository.findById(categoryUuid))
                    .thenReturn(Optional.of(testCategory));
            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));

            // Act
            inventoryService.editProduct(testUuid, productSaveDto);

            // Assert
            verify(productRepository).existsByNameIgnoreCaseAndCategory_UuidCategoryAndUuidProductNot(
                    productSaveDto.getName(), categoryUuid, testUuid);
            verify(categoryRepository).findById(categoryUuid);
            verify(productRepository).findById(testUuid);
        }

        @Test
        @DisplayName("Should delete product (soft delete)")
        void shouldDeleteProduct() {
            // Arrange
            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class)))
                    .thenReturn(testProduct);

            // Act
            inventoryService.deleteProduct(testUuid);

            // Assert
            verify(productRepository).findById(testUuid);
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should stock product successfully (IN)")
        void shouldStockProductSuccessfully() {
            // Arrange
            BigDecimal stockQty = new BigDecimal("5");
            when(productRepository.existsById(testUuid)).thenReturn(true);

            // Act
            inventoryService.stockProduct(testUuid, stockQty);

            // Assert
            verify(productRepository).existsById(testUuid);
            verify(productRepository).incrementStock(testUuid, stockQty);
        }

        @Test
        @DisplayName("Should subtract stock when quantity is negative")
        void shouldSubtractStockSuccessfully() {
            // Arrange
            BigDecimal stockQty = new BigDecimal("-3");
            when(productRepository.existsById(testUuid)).thenReturn(true);

            // Act
            inventoryService.stockProduct(testUuid, stockQty);

            // Assert
            verify(productRepository).existsById(testUuid);
            verify(productRepository).incrementStock(testUuid, stockQty);
        }

        @Test
        @DisplayName("Should throw NotFoundException when stocking non-existing product")
        void shouldThrowNotFoundExceptionWhenStockingInvalidProduct() {
            // Arrange
            when(productRepository.existsById(testUuid)).thenReturn(false);

            // Act & Assert
            assertThrows(
                    NotFoundException.class,
                    () -> inventoryService.stockProduct(testUuid, BigDecimal.ONE)
            );

            verify(productRepository).existsById(testUuid);
            verify(productRepository, never()).incrementStock(any(), any());
        }

        @Test
        @DisplayName("Should throw ValidationException when quantity is zero")
        void shouldThrowValidationExceptionForZeroQuantity() {
            assertThrows(
                    ConflictException.class,
                    () -> inventoryService.stockProduct(testUuid, BigDecimal.ZERO)
            );

            verifyNoInteractions(productRepository);
        }

    }

    @Nested
    @DisplayName("Category Tests")
    class CategoryTests {

        @Test
        @DisplayName("Should get all categories")
        void shouldGetAllCategories() {
            // Arrange
            List<Category> categories = List.of(testCategory);
            when(categoryRepository.findAll(any(Sort.class)))
                    .thenReturn(categories);

            // Act
            List<CategoryDto> result = inventoryService.getCategories();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Electronics", result.get(0).getCategoryName());
            verify(categoryRepository).findAll(any(Sort.class));
        }

        @Test
        @DisplayName("Should get single category by UUID")
        void shouldGetCategoryByUuid() {
            // Arrange
            when(categoryRepository.findById(categoryUuid))
                    .thenReturn(Optional.of(testCategory));

            // Act
            CategoryDto result = inventoryService.getCategory(categoryUuid);

            // Assert
            assertNotNull(result);
            assertEquals("Electronics", result.getCategoryName());
            verify(categoryRepository).findById(categoryUuid);
        }

        @Test
        @DisplayName("Should create new category")
        void shouldCreateNewCategory() {
            // Arrange
            CategoryDto categoryDto = new CategoryDto("Food");
            when(categoryRepository.existsByCategoryNameIgnoreCase(anyString()))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(testCategory);

            // Act
            inventoryService.newCategory(categoryDto);

            // Assert
            verify(categoryRepository).existsByCategoryNameIgnoreCase("Food");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw ConflictException when category already exists")
        void shouldThrowConflictExceptionWhenCategoryExists() {
            // Arrange
            CategoryDto categoryDto = new CategoryDto("Electronics");
            when(categoryRepository.existsByCategoryNameIgnoreCase(anyString()))
                    .thenReturn(true);

            // Act & Assert
            assertThrows(ConflictException.class,
                    () -> inventoryService.newCategory(categoryDto));
            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("Should update category")
        void shouldUpdateCategory() {
            // Arrange
            CategoryDto categoryDto = new CategoryDto("Updated Category");
            when(categoryRepository.findById(categoryUuid))
                    .thenReturn(Optional.of(testCategory));

            // Act
            inventoryService.updateCategory(categoryUuid, categoryDto);

            // Assert
            verify(categoryRepository).findById(categoryUuid);
            assertEquals("Updated Category", testCategory.getCategoryName());
        }

        @Test
        @DisplayName("Should delete category when no products exist")
        void shouldDeleteCategoryWhenNoProducts() {
            // Arrange
            when(categoryRepository.existsByUuidCategoryAndIsDeletedFalse(categoryUuid))
                    .thenReturn(false);
            when(categoryRepository.findById(categoryUuid))
                    .thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(testCategory);

            // Act
            inventoryService.deleteCategory(categoryUuid);

            // Assert
            verify(categoryRepository).existsByUuidCategoryAndIsDeletedFalse(categoryUuid);
            verify(categoryRepository).findById(categoryUuid);
            verify(categoryRepository).save(testCategory);
        }

        @Test
        @DisplayName("Should throw ConflictException when deleting category with products")
        void shouldThrowConflictExceptionWhenDeletingCategoryWithProducts() {
            // Arrange
            when(categoryRepository.existsByUuidCategoryAndIsDeletedFalse(categoryUuid))
                    .thenReturn(true);

            // Act & Assert
            assertThrows(ConflictException.class,
                    () -> inventoryService.deleteCategory(categoryUuid));
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Inventory Transaction Tests")
    class InventoryTransactionTests {

        @Test
        @DisplayName("Should record IN transaction successfully")
        void shouldRecordInTransaction() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.valueOf(50))
                    .inventoryTransactionType(InventoryTransactionType.IN)
                    .reference("PO-001")
                    .build();

            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));
            when(inventoryRepository.save(any(Inventory.class)))
                    .thenReturn(new Inventory());
            when(productRepository.save(any(Product.class)))
                    .thenReturn(testProduct);

            BigDecimal initialQuantity = testProduct.getQuantity();

            // Act
            inventoryService.RecordInventoryTransaction(dto);

            // Assert
            assertEquals(initialQuantity.add(BigDecimal.valueOf(50)),
                    testProduct.getQuantity());
            verify(inventoryRepository).save(any(Inventory.class));
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should record OUT transaction successfully")
        void shouldRecordOutTransaction() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.valueOf(30))
                    .inventoryTransactionType(InventoryTransactionType.OUT)
                    .reference("SO-001")
                    .build();

            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));
            when(inventoryRepository.save(any(Inventory.class)))
                    .thenReturn(new Inventory());
            when(productRepository.save(any(Product.class)))
                    .thenReturn(testProduct);

            BigDecimal initialQuantity = testProduct.getQuantity();

            // Act
            inventoryService.RecordInventoryTransaction(dto);

            // Assert
            assertEquals(initialQuantity.subtract(BigDecimal.valueOf(30)),
                    testProduct.getQuantity());
            verify(inventoryRepository).save(any(Inventory.class));
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should throw exception when OUT quantity exceeds stock")
        void shouldThrowExceptionWhenOutQuantityExceedsStock() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.valueOf(200))
                    .inventoryTransactionType(InventoryTransactionType.OUT)
                    .reference("SO-002")
                    .build();

            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));

            // Act & Assert
            assertThrows(IllegalStateException.class,
                    () -> inventoryService.RecordInventoryTransaction(dto));
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should record ADJUSTMENT transaction successfully")
        void shouldRecordAdjustmentTransaction() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.valueOf(80))
                    .inventoryTransactionType(InventoryTransactionType.ADJUSTMENT)
                    .reference("ADJ-001")
                    .build();

            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.of(testProduct));
            when(inventoryRepository.save(any(Inventory.class)))
                    .thenReturn(new Inventory());
            when(productRepository.save(any(Product.class)))
                    .thenReturn(testProduct);

            // Act
            inventoryService.RecordInventoryTransaction(dto);

            // Assert
            assertEquals(BigDecimal.valueOf(80), testProduct.getQuantity());
            verify(inventoryRepository).save(any(Inventory.class));
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should throw exception when transaction type is null")
        void shouldThrowExceptionWhenTransactionTypeIsNull() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.valueOf(50))
                    .inventoryTransactionType(null)
                    .reference("REF-001")
                    .build();

            // Act & Assert
            assertThrows(ConflictException.class,
                    () -> inventoryService.RecordInventoryTransaction(dto));
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw exception when quantity is zero or negative")
        void shouldThrowExceptionWhenQuantityIsInvalid() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.ZERO)
                    .inventoryTransactionType(InventoryTransactionType.IN)
                    .reference("REF-002")
                    .build();

            // Act & Assert
            assertThrows(ConflictException.class,
                    () -> inventoryService.RecordInventoryTransaction(dto));
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw NotFoundException when product not found")
        void shouldThrowNotFoundExceptionWhenProductNotFoundForTransaction() {
            // Arrange
            InventoryTransactionRequestDto dto = InventoryTransactionRequestDto.builder()
                    .uuidProduct(testUuid)
                    .quantity(BigDecimal.valueOf(50))
                    .inventoryTransactionType(InventoryTransactionType.IN)
                    .reference("REF-003")
                    .build();

            when(productRepository.findById(testUuid))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class,
                    () -> inventoryService.RecordInventoryTransaction(dto));
            verify(inventoryRepository, never()).save(any(Inventory.class));
        }
    }
}