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
@AllArgsConstructor
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

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private ItemType itemType = ItemType.RESALE;

    @Builder.Default
    private VatType vatType = VatType.VATABLE;

    @NotNull
    private UUID categoryId;

}
