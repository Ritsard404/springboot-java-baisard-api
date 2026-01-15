package com.ritsard.baisard.cashier;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import com.ritsard.baisard.domain.member.entity.Timestamp;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.member.repository.TimestampRepository;
import com.ritsard.baisard.domain.member.service.CashierServiceImpl;
import com.ritsard.baisard.domain.order.entity.Invoice;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.domain.order.repository.InvoiceRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashierServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TimestampRepository timestampRepository;

    @Mock
    private PosTerminalInfoRepository posTerminalInfoRepository;

    @Mock
    private BaseMemberRepository<Member> baseMemberRepository;

    @Mock
    private AuthManager<Member> authManager;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private CashierServiceImpl cashierService;

    private Member cashier;
    private Member manager;
    private Timestamp timestamp;
    private PosTerminalInfo posTerminal;

    @BeforeEach
    void setUp() {
        // Setup cashier
        cashier = Member.builder()
                .build();

        // Setup manager
        manager = Member.builder()
                .build();

        // Setup POS terminal
        posTerminal = PosTerminalInfo.builder()
                .isTrainMode(false)
                .build();

        // Setup timestamp
        timestamp = Timestamp.builder()
                .timestampIn(Instant.now().minusSeconds(3600))
                .cashInDrawerAmount(new BigDecimal("1000.00"))
                .withdrawnDrawerAmount(BigDecimal.ZERO)
                .withdrawnDrawerCount(BigDecimal.ZERO)
                .cashier(cashier)
                .posTerminal(posTerminal)
                .build();
    }

    // ==================== cashInDrawer Tests ====================

    @Test
    @DisplayName("cashInDrawer - Should set cash amount successfully")
    void cashInDrawer_shouldSetCashAmountSuccessfully() {
        // Given
        BigDecimal amount = new BigDecimal("1000.00");
        when(authManager.getMember()).thenReturn(cashier);
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));

        // When
        cashierService.cashInDrawer(amount);

        // Then
        verify(timestampRepository, times(1)).save(timestamp);
        assertEquals(amount, timestamp.getCashInDrawerAmount());
    }

    @Test
    @DisplayName("cashInDrawer - Should throw exception when amount is null")
    void cashInDrawer_shouldThrowExceptionWhenAmountIsNull() {
        // When & Then
        assertThrows(ConflictException.class, () ->
                cashierService.cashInDrawer(null));
    }

    @Test
    @DisplayName("cashInDrawer - Should throw exception when amount is zero")
    void cashInDrawer_shouldThrowExceptionWhenAmountIsZero() {
        // When & Then
        assertThrows(ConflictException.class, () ->
                cashierService.cashInDrawer(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("cashInDrawer - Should throw exception when amount is negative")
    void cashInDrawer_shouldThrowExceptionWhenAmountIsNegative() {
        // When & Then
        assertThrows(ConflictException.class, () ->
                cashierService.cashInDrawer(new BigDecimal("-100.00")));
    }

    @Test
    @DisplayName("cashInDrawer - Should throw NotFoundException when no active timestamp")
    void cashInDrawer_shouldThrowNotFoundExceptionWhenNoActiveTimestamp() {
        // Given
        BigDecimal amount = new BigDecimal("1000.00");
        when(authManager.getMember()).thenReturn(cashier);
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                cashierService.cashInDrawer(amount));
    }

    // ==================== isCashedDrawer Tests ====================

    @Test
    @DisplayName("isCashedDrawer - Should return true when cash is in drawer")
    void isCashedDrawer_shouldReturnTrueWhenCashInDrawer() {
        // Given
        when(authManager.getMember()).thenReturn(cashier);
        when(timestampRepository.existsByCashierAndTimestampOutIsNullAndCashInDrawerAmountGreaterThan(
                cashier, BigDecimal.ZERO))
                .thenReturn(true);

        // When
        Boolean result = cashierService.isCashedDrawer();

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("isCashedDrawer - Should return false when no cash in drawer")
    void isCashedDrawer_shouldReturnFalseWhenNoCashInDrawer() {
        // Given
        when(authManager.getMember()).thenReturn(cashier);
        when(timestampRepository.existsByCashierAndTimestampOutIsNullAndCashInDrawerAmountGreaterThan(
                cashier, BigDecimal.ZERO))
                .thenReturn(false);

        // When
        Boolean result = cashierService.isCashedDrawer();

        // Then
        assertFalse(result);
    }

    // ==================== cashOutDrawer Tests ====================

    @Test
    @DisplayName("cashOutDrawer - Should process cash out successfully")
    void cashOutDrawer_shouldProcessCashOutSuccessfully() {
        // Given
        BigDecimal amount = new BigDecimal("1500.00");
        String managerIdentifier = "manager@example.com";

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));

        // When
        cashierService.cashOutDrawer(amount, managerIdentifier);

        // Then
        verify(timestampRepository, times(1)).save(timestamp);
        assertNotNull(timestamp.getTimestampOut());
        assertEquals(manager, timestamp.getManagerOut());
        assertEquals(amount, timestamp.getCashOutDrawerAmount());
    }

    @Test
    @DisplayName("cashOutDrawer - Should throw exception when amount is invalid")
    void cashOutDrawer_shouldThrowExceptionWhenAmountIsInvalid() {
        // Given
        String managerIdentifier = "manager@example.com";

        // When & Then
        assertThrows(ConflictException.class, () ->
                cashierService.cashOutDrawer(BigDecimal.ZERO, managerIdentifier));
    }

    @Test
    @DisplayName("cashOutDrawer - Should throw NotFoundException when no active timestamp")
    void cashOutDrawer_shouldThrowNotFoundExceptionWhenNoActiveTimestamp() {
        // Given
        BigDecimal amount = new BigDecimal("1500.00");
        String managerIdentifier = "manager@example.com";

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                cashierService.cashOutDrawer(amount, managerIdentifier));
    }

    // ==================== cashWithdrawDrawer Tests ====================

    @Test
    @DisplayName("cashWithdrawDrawer - Should process withdrawal successfully")
    void cashWithdrawDrawer_shouldProcessWithdrawalSuccessfully() {
        // Given
        BigDecimal withdrawAmount = new BigDecimal("200.00");
        String managerIdentifier = "manager@example.com";

        // Setup invoices with cash transactions
        Invoice invoice1 = createInvoice(
                new BigDecimal("500.00"),  // cashTendered
                new BigDecimal("50.00"),   // changeAmount
                BigDecimal.ZERO            // returnedAmount
        );
        Invoice invoice2 = createInvoice(
                new BigDecimal("300.00"),
                new BigDecimal("20.00"),
                BigDecimal.ZERO
        );
        List<Invoice> invoices = Arrays.asList(invoice1, invoice2);

        // Total cash in drawer = 1000 (starting) + (500-50) + (300-20) = 1730

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));
        when(invoiceRepository.findByCashierAndStatusAndCreatedAtAfterAndIsTrainMode(
                eq(cashier),
                eq(InvoiceStatusType.PAID),
                any(Instant.class),
                eq(false)))
                .thenReturn(invoices);

        // When
        cashierService.cashWithdrawDrawer(withdrawAmount, managerIdentifier);

        // Then
        verify(timestampRepository, times(1)).save(timestamp);
        assertEquals(withdrawAmount, timestamp.getWithdrawnDrawerAmount());
        assertEquals(BigDecimal.ONE, timestamp.getWithdrawnDrawerCount());
    }

    @Test
    @DisplayName("cashWithdrawDrawer - Should accumulate multiple withdrawals")
    void cashWithdrawDrawer_shouldAccumulateMultipleWithdrawals() {
        // Given
        BigDecimal firstWithdrawal = new BigDecimal("100.00");
        BigDecimal secondWithdrawal = new BigDecimal("150.00");
        String managerIdentifier = "manager@example.com";

        timestamp.setWithdrawnDrawerAmount(firstWithdrawal);
        timestamp.setWithdrawnDrawerCount(BigDecimal.ONE);

        List<Invoice> invoices = Arrays.asList(
                createInvoice(new BigDecimal("1000.00"), BigDecimal.ZERO, BigDecimal.ZERO)
        );

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));
        when(invoiceRepository.findByCashierAndStatusAndCreatedAtAfterAndIsTrainMode(
                any(), any(), any(), anyBoolean()))
                .thenReturn(invoices);

        // When
        cashierService.cashWithdrawDrawer(secondWithdrawal, managerIdentifier);

        // Then
        assertEquals(new BigDecimal("250.00"), timestamp.getWithdrawnDrawerAmount());
        assertEquals(new BigDecimal("2"), timestamp.getWithdrawnDrawerCount());
    }

    @Test
    @DisplayName("cashWithdrawDrawer - Should throw exception when manager is invalid")
    void cashWithdrawDrawer_shouldThrowExceptionWhenManagerIsInvalid() {
        // Given
        BigDecimal amount = new BigDecimal("200.00");
        String managerIdentifier = "invalid@example.com";

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ConflictException.class, () ->
                cashierService.cashWithdrawDrawer(amount, managerIdentifier));
    }

    @Test
    @DisplayName("cashWithdrawDrawer - Should throw exception when no active session")
    void cashWithdrawDrawer_shouldThrowExceptionWhenNoActiveSession() {
        // Given
        BigDecimal amount = new BigDecimal("200.00");
        String managerIdentifier = "manager@example.com";

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                cashierService.cashWithdrawDrawer(amount, managerIdentifier));
    }

    @Test
    @DisplayName("cashWithdrawDrawer - Should throw exception when cash in drawer is less than 100")
    void cashWithdrawDrawer_shouldThrowExceptionWhenCashInDrawerIsLessThan100() {
        // Given
        BigDecimal amount = new BigDecimal("50.00");
        String managerIdentifier = "manager@example.com";

        timestamp.setCashInDrawerAmount(new BigDecimal("50.00")); // Less than 100

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () ->
                cashierService.cashWithdrawDrawer(amount, managerIdentifier));

        assertTrue(exception.getMessage().contains("No active session or drawer amount not set"));
    }

    @Test
    @DisplayName("cashWithdrawDrawer - Should throw exception when withdrawal exceeds available cash")
    void cashWithdrawDrawer_shouldThrowExceptionWhenWithdrawalExceedsAvailableCash() {
        // Given
        BigDecimal excessiveAmount = new BigDecimal("5000.00");
        String managerIdentifier = "manager@example.com";

        List<Invoice> invoices = Arrays.asList(
                createInvoice(new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO)
        );

        // Total available = 1000 (starting) + 100 (from invoice) = 1100
        // Trying to withdraw 5000 should fail

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));
        when(invoiceRepository.findByCashierAndStatusAndCreatedAtAfterAndIsTrainMode(
                any(), any(), any(), anyBoolean()))
                .thenReturn(invoices);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () ->
                cashierService.cashWithdrawDrawer(excessiveAmount, managerIdentifier));

        assertTrue(exception.getMessage().contains("Cash amount exceeds available cash"));
    }

    @Test
    @DisplayName("cashWithdrawDrawer - Should handle train mode correctly")
    void cashWithdrawDrawer_shouldHandleTrainModeCorrectly() {
        // Given
        BigDecimal amount = new BigDecimal("200.00");
        String managerIdentifier = "manager@example.com";

        posTerminal.setTrainMode(true); // Set train mode

        List<Invoice> invoices = Arrays.asList(
                createInvoice(new BigDecimal("500.00"), BigDecimal.ZERO, BigDecimal.ZERO)
        );

        when(authManager.getMember()).thenReturn(cashier);
        when(baseMemberRepository.findWithDetailsByIdentifier(managerIdentifier))
                .thenReturn(Optional.of(manager));
        when(timestampRepository.findByCashierAndTimestampOutIsNull(cashier))
                .thenReturn(Optional.of(timestamp));
        when(invoiceRepository.findByCashierAndStatusAndCreatedAtAfterAndIsTrainMode(
                eq(cashier),
                eq(InvoiceStatusType.PAID),
                any(Instant.class),
                eq(true))) // Should use train mode = true
                .thenReturn(invoices);

        // When
        cashierService.cashWithdrawDrawer(amount, managerIdentifier);

        // Then
        verify(invoiceRepository).findByCashierAndStatusAndCreatedAtAfterAndIsTrainMode(
                eq(cashier),
                eq(InvoiceStatusType.PAID),
                any(Instant.class),
                eq(true));
    }

    // ==================== Helper Methods ====================

    private Invoice createInvoice(BigDecimal cashTendered, BigDecimal changeAmount, BigDecimal returnedAmount) {
        return Invoice.builder()
                .cashTendered(cashTendered)
                .changeAmount(changeAmount)
                .returnedAmount(returnedAmount)
                .status(InvoiceStatusType.PAID)
                .build();
    }
}