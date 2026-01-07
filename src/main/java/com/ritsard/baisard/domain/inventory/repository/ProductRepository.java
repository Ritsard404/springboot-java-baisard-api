package com.ritsard.baisard.domain.inventory.repository;

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

    @Query("SELECT p FROM Product p " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE %:keyword% OR LOWER(p.barcode) LIKE %:keyword%) " +
            "AND (:barcode IS NULL OR p.barcode = :barcode) " +
            "AND (:uuidCategory IS NULL OR p.category.uuidCategory = :uuidCategory) " +
            "AND p.isDeleted = false")
    Page<Product> findProductsWithConditions(
            @Param("keyword") String keyword,
            @Param("barcode") String barcode,
            @Param("uuidCategory") UUID uuidCategory,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity + :qty WHERE p.uuidProduct = :uuidProduct")
    void incrementStock(@Param("uuidProduct") UUID uuidProduct, @Param("qty") BigDecimal qty);

}
