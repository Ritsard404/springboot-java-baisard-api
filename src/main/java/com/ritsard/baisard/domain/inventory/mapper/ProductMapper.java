package com.ritsard.baisard.domain.inventory.mapper;

import com.ritsard.baisard.domain.inventory.dto.request.ProductSaveDto;
import com.ritsard.baisard.domain.inventory.entity.Category;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.global.utils.Formats;
import com.ritsard.baisard.global.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ImageUtils imageUtils;

    /**
     * Map DTO to new Product entity for creation.
     * Assumes category is already fetched from DB.
     */
    public static Product toEntity(ProductSaveDto dto, Category category, Company company) {
        if (dto == null || category == null) return null;

        return Product.builder()
                .name(Formats.capitalize(dto.getName()))
                .barcode(dto.getBarcode())
                .baseUnit(dto.getBaseUnit().toUpperCase())
                .quantity(dto.getQuantity())
                .cost(dto.getCost())
                .price(dto.getPrice())
                .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                .itemType(dto.getItemType())
                .vatType(dto.getVatType())
                .category(category)
                .company(company)
                .build();
    }

    /**
     * Map DTO to existing Product entity for update.
     * Assumes category is already fetched from DB.
     */
    public static void updateEntity(Product product, ProductSaveDto dto, Category category) {
        if (product == null || dto == null) return;

        product.setName(Formats.capitalize(dto.getName()));
        product.setBarcode(Objects.requireNonNullElse(dto.getBarcode(), ""));
        product.setBaseUnit(dto.getBaseUnit());
        product.setQuantity(dto.getQuantity());
        product.setCost(dto.getCost());
        product.setPrice(dto.getPrice());
        product.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : product.getIsAvailable());
        product.setItemType(dto.getItemType() != null ? dto.getItemType() : product.getItemType());
        product.setVatType(dto.getVatType() != null ? dto.getVatType() : product.getVatType());
        if (category != null) {
            product.setCategory(category);
        }
    }
}
