package com.ritsard.baisard.domain.inventory.dto.request;

import com.ritsard.baisard.domain.inventory.enums.InventoryTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory Request DTO")
public class InventoryTransactionRequestDto {
    @NotNull
    private InventoryTransactionType inventoryTransactionType;
    @NotNull
    private UUID uuidProduct;
    @NotNull
    private BigDecimal quantity;
    private String reference;
}
