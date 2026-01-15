package com.ritsard.baisard.domain.member.repository;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimestampRepository extends JpaRepository<Timestamp, UUID> {
    Optional<Timestamp> findByCashierAndTimestampOutIsNull(Member cashier);

    boolean existsByCashierAndTimestampOutIsNullAndCashInDrawerAmountGreaterThan(
            Member cashier,
            BigDecimal amount
    );
}
