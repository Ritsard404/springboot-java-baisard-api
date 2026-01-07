package com.ritsard.baisard.domain.order.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Invoice extends BaseEntity {

    /* =========================
       Primary Key
       ========================= */

    @Id
    @Column(name = "uuid_invoice", nullable = false, updatable = false)
    @Builder.Default
    private UUID uuidInvoice = UUIDManager.generateUUIDv7();

    /* =========================
       Invoice Identifiers
       ========================= */

    @NotNull
    @Column(name = "invoice_number", nullable = false, unique = true)
    private Long invoiceNumber;

    /* =========================
       Amounts
       ========================= */

    @NotNull
    @Column(name = "gross_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossAmount;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "sub_total", precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "cash_tendered", precision = 15, scale = 2)
    private BigDecimal cashTendered;

    @Column(name = "due_amount", precision = 15, scale = 2)
    private BigDecimal dueAmount;

    @Column(name = "total_tendered", precision = 15, scale = 2)
    private BigDecimal totalTendered;

    @Column(name = "change_amount", precision = 15, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "vat_sales", precision = 15, scale = 2)
    private BigDecimal vatSales;

    @Column(name = "vat_exempt", precision = 15, scale = 2)
    private BigDecimal vatExempt;

    @Column(name = "vat_amount", precision = 15, scale = 2)
    private BigDecimal vatAmount;

    @Column(name = "vat_zero", precision = 15, scale = 2)
    private BigDecimal vatZero;

    /* =========================
       Customer & Discount
       ========================= */

    @Column(name = "customer_name", nullable = false)
    @Builder.Default
    private String customerName = "Walk-in Customer";

    @Column(name = "eligible_disc_name")
    private String eligibleDiscName;

    @Column(name = "osca_id_num")
    private String oscaIdNum;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    /* =========================
       Return / Void
       ========================= */

    @Column(name = "returned_amount", precision = 15, scale = 2)
    private BigDecimal returnedAmount;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voided_by")
    private Member voidedBy;

    /* =========================
       Relations
       ========================= */

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private Member cashier;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<EPayment> ePayments = new ArrayList<>();

    /* =========================
       Status & Metadata
       ========================= */


    @NotNull
    @Column(name = "status", nullable = false)
    private InvoiceStatusType status;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "is_train_mode", nullable = false)
    @Builder.Default
    private boolean isTrainMode = false;
}
