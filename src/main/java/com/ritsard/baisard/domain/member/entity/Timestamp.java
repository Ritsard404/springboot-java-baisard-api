package com.ritsard.baisard.domain.member.entity;

import com.ritsard.baisard.utils.helper.UUIDManager;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "timestamp")
public class Timestamp {

    @Id
    @Column(name = "uuid_timestamp", nullable = false)
    @Builder.Default
    private UUID uuidTimestamp = UUIDManager.generateUUIDv7();

    @Column(name = "timestamp_in")
    private Instant timestampIn;

    @Column(name = "timestamp_out")
    private Instant timestampOut;

    @Column(name = "cash_in_drawer_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cashInDrawerAmount = BigDecimal.ZERO;

    @Column(name = "cash_out_drawer_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cashOutDrawerAmount = BigDecimal.ZERO;

    @Column(name = "withdrawn_drawer_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal withdrawnDrawerAmount = BigDecimal.ZERO;

    @Column(name = "withdrawn_drawer_count", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal withdrawnDrawerCount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_pos_terminal", nullable = false)
    private PosTerminalInfo posTerminal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_cashier", nullable = false)
    private Member cashier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_manager_in")
    private Member managerIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid_manager_out")
    private Member managerOut;
}
