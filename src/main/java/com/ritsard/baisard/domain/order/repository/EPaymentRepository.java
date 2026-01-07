package com.ritsard.baisard.domain.order.repository;

import com.ritsard.baisard.domain.order.entity.EPayment;
import com.ritsard.baisard.domain.order.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface EPaymentRepository extends JpaRepository<EPayment, UUID> {
}
