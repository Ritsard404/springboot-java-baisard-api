package com.ritsard.baisard.domain.order.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.global.utils.Formats;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item")
public class Item extends BaseEntity {

    @Id
    @Column(name = "uuid_item", nullable = false)
    @Builder.Default
    private UUID uuidItem = UUIDManager.generateUUIDv7();

    @Column(name = "qty", precision = 19, scale = 4, nullable = false)
    private BigDecimal qty;

    @Column(name = "price", precision = 19, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "subtotal", precision = 19, scale = 4, nullable = false)
    private BigDecimal subTotal;

    @Column(name = "status", length = 50, nullable = false)
    private InvoiceStatusType status;

    @Column(name = "is_training_mode")
    @Builder.Default
    private Boolean isTrainingMode = false;


    public String getQtyDisplay() {
        if (qty == null) return "0";

        // Format quantity: integer if no decimal, else 2 decimals
        String baseQty = qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0
                ? String.valueOf(qty.intValue())
                : qty.setScale(2, RoundingMode.HALF_UP).toString();

        // Prefix "R" if status is RETURNED
        if (status == InvoiceStatusType.RETURNED) {
            return "R" + baseQty;
        }

        return baseQty;
    }


    public String getDisplayNameWithPrice() {
        if (product == null || product.getName() == null || price == null) return "";
        return product.getName() + " @" + Formats.pesoFormat(price);
    }


    public String getDisplayPrice() {
        if (price == null) return "0.00";
        return Formats.pesoFormat(price);
    }


    public String getDisplaySubtotalVat() {
        if (subTotal == null || product == null || product.getVatType() == null) return "0.00Z";

        switch (product.getVatType()) {
            case VATABLE:
                return Formats.pesoFormat(subTotal) + "V";
            case EXEMPT:
                return Formats.pesoFormat(subTotal) + "E";
            default:
                return Formats.pesoFormat(subTotal) + "Z";
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_product", nullable = false)
    private Product product;

}
