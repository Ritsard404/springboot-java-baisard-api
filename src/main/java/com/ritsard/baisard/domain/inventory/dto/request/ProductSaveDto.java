package com.ritsard.baisard.domain.inventory.dto.request;

import com.ritsard.baisard.domain.inventory.enums.ItemType;
import com.ritsard.baisard.domain.inventory.enums.VatType;
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

    @NotNull
    private Boolean isAvailable = true;

    @NotNull
    private ItemType itemType = ItemType.RESALE;

    @NotNull
    private VatType vatType = VatType.VATABLE;

    @NotNull
    private UUID uuidCategory;

}
