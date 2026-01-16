package com.ritsard.baisard.domain.inventory.dto.response;

import com.ritsard.baisard.domain.inventory.enums.ItemType;
import com.ritsard.baisard.domain.inventory.enums.VatType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Schema(description = "Product DTO")
public class ProductDto {

    private UUID uuidProduct;

    @NotBlank
    private String name;

    private String productImageUrl;

    private String barcode;

    @NotBlank
    private String baseUnit;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal cost;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    private Boolean isAvailable;

    private ItemType itemType;

    private VatType vatType;

    @NotNull
    private UUID categoryId;
    @NotBlank
    private String categoryName;

    // Explicit constructor for JPQL mapping
    public ProductDto(
            UUID uuidProduct,
            String name,
            String productImageUrl,
            String barcode,
            String baseUnit,
            BigDecimal quantity,
            BigDecimal cost,
            BigDecimal price,
            Boolean isAvailable,
            ItemType itemType,
            VatType vatType,
            UUID categoryId,
            String categoryName
    ) {
        this.uuidProduct = uuidProduct;
        this.name = name;
        this.productImageUrl = productImageUrl;
        this.barcode = barcode;
        this.baseUnit = baseUnit;
        this.quantity = quantity;
        this.cost = cost;
        this.price = price;
        this.isAvailable = isAvailable;
        this.itemType = itemType;
        this.vatType = vatType;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
