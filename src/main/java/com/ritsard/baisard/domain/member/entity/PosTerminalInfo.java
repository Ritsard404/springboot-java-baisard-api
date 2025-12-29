package com.ritsard.baisard.domain.member.entity;

import com.ritsard.baisard.base.entity.BaseEntity;
import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "pos_terminal_info")
public class PosTerminalInfo extends BaseEntity {

    @Id
    @Column(name = "uuid_pos_terminal", nullable = false)
    @Builder.Default
    private UUID uuidPosTerminal = UUIDManager.generateUUIDv7();

    // POS Machine Info
    @Column(name = "min_number", nullable = false)
    private String minNumber;

    @Column(name = "accreditation_number", nullable = false)
    private String accreditationNumber;

    @Column(name = "ptu_number", nullable = false)
    private String ptuNumber;

    @Column(name = "date_issued", nullable = false)
    private LocalDate dateIssued;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    // Business Details
    @Column(name = "pos_name", nullable = false)
    private String posName;

    @Column(name = "registered_name", nullable = false)
    private String registeredName;

    @Column(name = "operated_by", nullable = false)
    private String operatedBy;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "vat_tin_number", nullable = false)
    private String vatTinNumber;

    @Column(name = "vat", nullable = false)
    private int vat;

    @Column(name = "discount_max", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountMax;

    // API Flags
    @Column(name = "cost_center", nullable = false)
    private String costCenter;

    @Column(name = "branch_center", nullable = false)
    private String branchCenter;

    @Column(name = "use_center", nullable = false)
    private String useCenter;

    @Column(name = "db_name", nullable = false)
    private String dbName;

    @Column(name = "printer_name", nullable = false)
    private String printerName;

    // Counters
    @Column(name = "reset_counter_no")
    @Builder.Default
    private int resetCounterNo = 0;

    @Column(name = "reset_counter_train_no")
    @Builder.Default
    private int resetCounterTrainNo = 0;

    @Column(name = "z_counter_no")
    @Builder.Default
    private int zCounterNo = 0;

    @Column(name = "z_counter_train_no")
    @Builder.Default
    private int zCounterTrainNo = 0;

    // Modes
    @Column(name = "is_train_mode")
    @Builder.Default
    private boolean isTrainMode = false;

    @Column(name = "is_retail_type")
    @Builder.Default
    private boolean isRetailType = false;

    // Company relation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
