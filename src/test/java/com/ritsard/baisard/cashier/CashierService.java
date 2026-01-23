package com.ritsard.baisard.cashier;

import com.ritsard.baisard.domain.member.dto.response.CashierInfoDto;
import com.ritsard.baisard.domain.member.dto.response.MyCashiersDto;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import com.ritsard.baisard.domain.member.entity.Timestamp;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.member.repository.TimestampRepository;
import com.ritsard.baisard.domain.member.repository.projections.MyCashiersProjection;
import com.ritsard.baisard.domain.member.service.CashierServiceImpl;
import com.ritsard.baisard.domain.order.entity.Invoice;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.domain.order.repository.InvoiceRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.jwt.model.entity.LoginCredential;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepository;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
//
//    @Test
//    @DisplayName("myCashiers - Should return paged DTOs successfully")
//    void myCashiers_shouldReturnPagedData() {
//        // Given
//        String keyword = "test";
//        UUID companyId = UUID.randomUUID();
//
//        // Mock Admin with Company
//        com.ritsard.baisard.domain.member.entity.Company mockCompany = mock(com.ritsard.baisard.domain.member.entity.Company.class);
//        when(mockCompany.getUuidCompany()).thenReturn(companyId);
//        cashier.setCompany(mockCompany);
//
//        when(authManager.getMember()).thenReturn(cashier);
//
//        // Mock Projection and Page
//        MyCashiersProjection projection = mock(MyCashiersProjection.class);
//        Page<MyCashiersProjection> projectionPage = new PageImpl<>(List.of(projection));
//
//        when(memberRepository.findMyCashiersWithProjection(eq(keyword), eq(companyId), any(Pageable.class)))
//                .thenReturn(projectionPage);
//
//        // When
//        Page<MyCashiersDto> result = cashierService.myCashiers(keyword, 0, 10, "createdAt", "asc");
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getContent().size());
//        verify(memberRepository).findMyCashiersWithProjection(eq(keyword), eq(companyId), any(Pageable.class));
//    }
//
//    @Test
//    @DisplayName("myCashiers - Should throw exception if admin has no company")
//    void myCashiers_shouldThrowExceptionWhenNoCompany() {
//        // Given
//        when(authManager.getMember()).thenReturn(cashier);
//        cashier.setCompany(null); // No company associated
//
//        // When & Then
//        assertThrows(ConflictException.class, () ->
//                cashierService.myCashiers("key", 0, 10, null, null));
//    }
//
//    @Test
//    @DisplayName("updateCashierInfo - Should update successfully")
//    void updateCashierInfo_shouldUpdateSuccessfully() {
//        // Given
//        UUID cashierId = UUID.randomUUID();
//
//        // Using Builder to avoid constructor visibility issues
//        CashierInfoDto dto = CashierInfoDto.builder()
//                .cashierId(cashierId)
//                .name("Updated Name")
//                .isActive(true)
//                .identifier("new@email.com")
//                .build();
//
//        // Ensure cashier has a credential to update
//        cashier.getLoginCredentials().add(LoginCredential.builder()
//                .identifier("old@email.com")
//                .member(cashier)
//                .build());
//
//        when(memberRepository.findById(cashierId)).thenReturn(Optional.of(cashier));
//
//        // When
//        cashierService.updateCashierInfo(dto);
//
//        // Then
//        verify(memberRepository, times(1)).findById(cashierId);
//        assertEquals("Updated Name", cashier.getName());
//        assertEquals("new@email.com", cashier.getIdentifier());
//    }
//
//    @Test
//    @DisplayName("cashierInfo - Should return DTO when cashier exists")
//    void cashierInfo_shouldReturnDtoWhenCashierExists() {
//        // Given
//        UUID cashierId = UUID.randomUUID();
//        cashier.getLoginCredentials().add(LoginCredential.builder()
//                .identifier("cashier@test.com")
//                .member(cashier)
//                .build());
//        when(memberRepository.findById(cashierId)).thenReturn(Optional.of(cashier));
//
//        // When
//        CashierInfoDto result = cashierService.cashierInfo(cashierId);
//
//        // Then
//        assertNotNull(result);
//        verify(memberRepository, times(1)).findById(cashierId);
//    }
//
//    @Test
//    @DisplayName("cashierInfo - Should throw NoSuchUserException when cashier not found")
//    void cashierInfo_shouldThrowExceptionWhenNotFound() {
//        // Given
//        UUID cashierId = UUID.randomUUID();
//        when(memberRepository.findById(cashierId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(NoSuchUserException.class, () -> cashierService.cashierInfo(cashierId));
//    }
}