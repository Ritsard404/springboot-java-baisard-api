package com.ritsard.baisard.domain.order.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "e_payment")
public class EPayment extends BaseEntity {
    @Id
    @Column(name = "uuid_e_payment", nullable = false)
    @Builder.Default
    private UUID uuidEPayment = UUIDManager.generateUUIDv7();

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "amount", nullable = false, unique = true)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_invoice", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_sale_type", nullable = false)
    private SaleType saleType;
}
