package com.ritsard.baisard.domain.inventory.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.inventory.enums.InventoryTransactionType;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory")
public class Inventory extends BaseEntity {
    @Id
    @Column(name = "uuid_inventory", nullable = false)
    @Builder.Default
    private UUID uuidInventory = UUIDManager.generateUUIDv7();

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private InventoryTransactionType type; // IN or OUT

    @Column(name = "reference")
    private String reference; // Invoice number, reason, etc.

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uuid_product", nullable = false)
    private Product product;
}
