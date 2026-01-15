package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.Timestamp;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.member.repository.TimestampRepository;
import com.ritsard.baisard.domain.order.entity.Invoice;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.domain.order.repository.InvoiceRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CashierServiceImpl implements CashierService {
    private final MemberRepository memberRepository;
    private final TimestampRepository timestampRepository;
    private final PosTerminalInfoRepository posTerminalInfoRepository;
    private final InvoiceRepository invoiceRepository;
    private final BaseMemberRepository<Member> baseMemberRepository;
    private final AuthManager<Member> authManager;

    @Override
    public void cashInDrawer(BigDecimal amount) {
        validateAmount(amount);
        Member cashier = authManager.getMember();

        Timestamp timestamp = timestampRepository
                .findByCashierAndTimestampOutIsNull(cashier)
                .orElseThrow(() -> new NotFoundException("Cashier not found."));

        timestamp.setCashInDrawerAmount(amount);
        timestampRepository.save(timestamp);

    }

    @Override
    public Boolean isCashedDrawer() {
        Member cashier = authManager.getMember();

        return timestampRepository
                .existsByCashierAndTimestampOutIsNullAndCashInDrawerAmountGreaterThan(
                        cashier,
                        BigDecimal.ZERO
                );
    }

    @Override
    public void cashOutDrawer(BigDecimal amount, String managerIdentifier) {
        validateAmount(amount);
        Member cashier = authManager.getMember();

        Optional<Member> manager = baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier);

        Timestamp timestamp = timestampRepository
                .findByCashierAndTimestampOutIsNull(cashier)
                .orElseThrow(() -> new NotFoundException("Cashier not found."));

        timestamp.setTimestampOut(Instant.now());
        timestamp.setManagerOut(manager.orElse(null));
        timestamp.setCashOutDrawerAmount(amount);
        timestampRepository.save(timestamp);
    }

    @Override
    public void cashWithdrawDrawer(BigDecimal amount, String managerIdentifier) {
        validateAmount(amount);

        Member cashier = authManager.getMember();

        // Validate manager
        Member manager = baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier)
                .orElseThrow(() -> new ConflictException("Invalid manager credential."));

        // Find active timestamp for cashier (which includes PosTerminalInfo)
        Timestamp timestamp = timestampRepository
                .findByCashierAndTimestampOutIsNull(cashier)
                .orElseThrow(() -> new NotFoundException("No active session found for this cashier."));

        // Get train mode from the POS terminal associated with this timestamp
        boolean isTrainMode = timestamp.getPosTerminal().isTrainMode();

        // Validate cash in drawer
        BigDecimal startingCash = timestamp.getCashInDrawerAmount();
        if (startingCash == null || startingCash.compareTo(new BigDecimal("100")) < 0) {
            throw new ConflictException("No active session or drawer amount not set.");
        }

        Instant tsIn = timestamp.getTimestampIn();

        // Fetch all paid invoices for this cashier since clock-in
        List<Invoice> paidInvoices = invoiceRepository
                .findByCashierAndStatusAndCreatedAtAfterAndIsTrainMode(
                        cashier,
                        InvoiceStatusType.PAID,
                        tsIn,
                        isTrainMode
                );

        // Calculate total cash in drawer from paid invoices
        BigDecimal totalCashInDrawer = paidInvoices.stream()
                .map(invoice -> {
                    BigDecimal cashTendered = Optional.ofNullable(invoice.getCashTendered())
                            .orElse(BigDecimal.ZERO);
                    BigDecimal changeAmount = Optional.ofNullable(invoice.getChangeAmount())
                            .orElse(BigDecimal.ZERO);
                    BigDecimal returnedAmount = Optional.ofNullable(invoice.getReturnedAmount())
                            .orElse(BigDecimal.ZERO);

                    return cashTendered.subtract(changeAmount).subtract(returnedAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validate withdrawal amount
        BigDecimal availableCash = startingCash.add(totalCashInDrawer);
        if (amount.compareTo(availableCash) > 0) {
            throw new ConflictException(
                    "Cash amount exceeds available cash in drawer and pending orders total."
            );
        }

        // Update timestamp
        BigDecimal currentWithdrawn = timestamp.getWithdrawnDrawerAmount();
        timestamp.setWithdrawnDrawerAmount(currentWithdrawn.add(amount));

        BigDecimal currentCount = timestamp.getWithdrawnDrawerCount();
        timestamp.setWithdrawnDrawerCount(currentCount.add(BigDecimal.ONE));

        timestampRepository.save(timestamp);

        log.info("Cash withdrawn from drawer: {} by cashier: {} approved by manager: {}",
                amount, cashier.getIdentifier(), manager.getIdentifier());
    }


    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictException("Amount must be greater than zero.");
        }
    }
}
