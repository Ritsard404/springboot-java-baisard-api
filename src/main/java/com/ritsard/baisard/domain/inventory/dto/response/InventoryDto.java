package com.ritsard.baisard.domain.inventory.dto.response;

import com.ritsard.baisard.domain.inventory.enums.InventoryTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory DTO")
public class InventoryDto {
    private UUID uuidInventory;
    private BigDecimal quantity;
    private InventoryTransactionType type;
    private String reference;

    private UUID uuidProduct;
    private String name;

}
