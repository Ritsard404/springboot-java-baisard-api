package com.ritsard.baisard.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item Request DTO")
public class ItemRequestDto {
    @NotNull(message = "Product UUID is required")
    private UUID uuidProduct;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    private BigDecimal qty;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", message = "Subtotal must be non-negative")
    private BigDecimal subTotal;

    @Schema(description = "Status of the item")
    @NotNull(message = "Item status is required")
    @Enumerated(EnumType.STRING)
    private ItemStatusType itemStatusType;
}
