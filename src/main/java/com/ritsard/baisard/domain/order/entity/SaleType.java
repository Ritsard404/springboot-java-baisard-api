package com.ritsard.baisard.domain.order.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.order.entity.enums.SaleTypeEnums;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sale_type")
public class SaleType extends BaseEntity {
    @Id
    @Column(name = "uuid_sale_type", nullable = false)
    @Builder.Default
    private UUID uuidSaleType = UUIDManager.generateUUIDv7();

    @Column(name = "name")
    private String name;

    @Column(name = "account")
    private String account;

    @Column(name = "type")
    @Builder.Default
    private SaleTypeEnums type = SaleTypeEnums.EPAYMENT;

}
