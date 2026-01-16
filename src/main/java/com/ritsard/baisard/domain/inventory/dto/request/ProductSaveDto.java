package com.ritsard.baisard.domain.inventory.dto.request;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSaveDto {
    @NotBlank
    private String name;

    @Schema(description = "Product Image Encrypted Id")
    private String encryptedProductImageId;

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

    @NotNull
    @Builder.Default
    private Boolean isAvailable = true;

    @NotNull
    @Builder.Default
    private ItemType itemType = ItemType.RESALE;

    @NotNull
    @Builder.Default
    private VatType vatType = VatType.VATABLE;

    @NotNull
    private UUID uuidCategory;

    @NotBlank
    private String categoryName;

}
