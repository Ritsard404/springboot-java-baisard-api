package com.ritsard.baisard.domain.inventory.repository;

import com.ritsard.baisard.domain.inventory.dto.response.ProductDto;
import com.ritsard.baisard.domain.inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByNameIgnoreCaseAndCategory_UuidCategory(String name, UUID categoryId);

    boolean existsByNameIgnoreCaseAndCategory_UuidCategoryAndUuidProductNot(
            String name,
            UUID categoryId,
            UUID uuidProduct
    );

//    @Query("SELECT p FROM Product p " +
//            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE %:keyword% OR LOWER(p.barcode) LIKE %:keyword%) " +
//            "AND (:barcode IS NULL OR p.barcode = :barcode) " +
//            "AND (:uuidCategory IS NULL OR p.category.uuidCategory = :uuidCategory) " +
//            "AND p.isDeleted = false")
//    Page<Product> findProductsWithConditions(
//            @Param("keyword") String keyword,
//            @Param("barcode") String barcode,
//            @Param("uuidCategory") UUID uuidCategory,
//            Pageable pageable
//    );

    @Query("""
                SELECT new com.ritsard.baisard.domain.inventory.dto.response.ProductDto(
                    p.uuidProduct,
                    p.name,
                    p.productImageUrl,
                    p.barcode,
                    p.baseUnit,
                    p.quantity,
                    p.cost,
                    p.price,
                    p.isAvailable,
                    p.itemType,
                    p.vatType,
                    p.category.uuidCategory,
                    p.category.categoryName
                )
                FROM Product p
                WHERE (CAST(:keyword AS string) IS NULL
                       OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) 
                       OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
                  AND (:barcode IS NULL OR p.barcode = :barcode)
                  AND (:uuidCategory IS NULL OR p.category.uuidCategory = :uuidCategory)
                  AND (:uuidCompany IS NULL OR p.company.uuidCompany = :uuidCompany OR p.company IS NULL) 
                  AND p.isDeleted = false 
            """)
    Page<ProductDto> findProductsWithConditions(
            @Param("keyword") String keyword,
            @Param("barcode") String barcode,
            @Param("uuidCategory") UUID uuidCategory,
            @Param("uuidCompany") UUID uuidCompany,
            Pageable pageable
    );


    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity + :qty WHERE p.uuidProduct = :uuidProduct")
    void incrementStock(@Param("uuidProduct") UUID uuidProduct, @Param("qty") BigDecimal qty);

}
