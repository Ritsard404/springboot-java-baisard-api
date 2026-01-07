package com.ritsard.baisard.order;

import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.enums.VatType;
import com.ritsard.baisard.domain.inventory.repository.ProductRepository;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.order.dto.request.ItemRequestDto;
import com.ritsard.baisard.domain.order.dto.request.OrderDto;
import com.ritsard.baisard.domain.order.entity.Invoice;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.domain.order.repository.EPaymentRepository;
import com.ritsard.baisard.domain.order.repository.InvoiceRepository;
import com.ritsard.baisard.domain.order.repository.SaleTypeRepository;
import com.ritsard.baisard.domain.order.service.OrderService;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.jwt.model.entity.Permission;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepositoryCustom;
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
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock private EPaymentRepository paymentRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private SaleTypeRepository saleTypeRepository;
    @Mock private ProductRepository productRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private PosTerminalInfoRepository terminalRepository;
    @Mock private AuthManager<Member> authManager;
    @Mock private BaseMemberRepositoryCustom<Member> memberBaseMemberRepositoryCustom;

    @InjectMocks
    private OrderService orderService;

    private Member cashier;
    private Member manager;
    private Company company;
    private PosTerminalInfo terminal;
    private Product product;
    private OrderDto orderDto;
    private UUID productUuid;

    @BeforeEach
    void setUp() {
        productUuid = UUID.randomUUID();

        company = Company.builder()
                .uuidCompany(UUID.randomUUID())
                .name("Test Company")
                .build();

        terminal = PosTerminalInfo.builder()
                .uuidPosTerminal(UUID.randomUUID())
                .company(company)
                .vat(12)
                .discountMax(BigDecimal.valueOf(1000))
                .isTrainMode(false)
                .isDeleted(false)
                .resetCounterNo(0)
                .resetCounterTrainNo(0)
                .dateIssued(LocalDate.now())
                .validUntil(LocalDate.now().plusYears(1))
                .build();

        cashier = Member.builder()
                .uuidMember(UUID.randomUUID())
                .name("Cashier")
                .company(company)
                .permissions(Set.of(
                        Permission.builder().permissionType("CASHIER").build()
                ))
                .build();

        manager = Member.builder()
                .uuidMember(UUID.randomUUID())
                .name("Manager")
                .company(company)
                .permissions(Set.of(
                        Permission.builder().permissionType("ADMIN").build()
                ))
                .build();

        product = Product.builder()
                .uuidProduct(productUuid)
                .name("Product")
                .price(BigDecimal.valueOf(100))
                .quantity(BigDecimal.valueOf(10))
                .vatType(VatType.VATABLE)
                .build();

        ItemRequestDto item = ItemRequestDto.builder()
                .uuidProduct(productUuid)
                .qty(BigDecimal.valueOf(2))
                .price(BigDecimal.valueOf(100))
                .subTotal(BigDecimal.valueOf(200))
                .build();

        orderDto = OrderDto.builder()
                .cashTenderAmount(BigDecimal.valueOf(300))
                .items(List.of(item))
                .build();
    }

    @Test
    @DisplayName("Pay order successfully (live mode)")
    void payOrder_success() {
        when(authManager.getMember()).thenReturn(cashier);
        when(terminalRepository.findAll()).thenReturn(List.of(terminal));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));
        when(invoiceRepository.findLastInvoiceNumberByTerminal(any(), anyBoolean()))
                .thenReturn(null);
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(terminalRepository.save(any(PosTerminalInfo.class)))
                .thenAnswer(i -> i.getArgument(0));

        orderService.payOrder(orderDto);

        assertEquals(BigDecimal.valueOf(8), product.getQuantity());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Throw when insufficient stock")
    void payOrder_insufficientStock() {
        product.setQuantity(BigDecimal.ONE);

        when(authManager.getMember()).thenReturn(cashier);
        when(terminalRepository.findAll()).thenReturn(List.of(terminal));
        when(productRepository.findAllById(any()))
                .thenReturn(List.of(product));

        assertThrows(ConflictException.class,
                () -> orderService.payOrder(orderDto));
    }

    @Test
    @DisplayName("Throw when cashier missing")
    void payOrder_noCashier() {
        when(authManager.getMember())
                .thenThrow(new NotFoundException("Cashier not found"));

        assertThrows(NotFoundException.class,
                () -> orderService.payOrder(orderDto));
    }

    @Test
    @DisplayName("Cancel order with manager")
    void cancelOrder_success() {
        when(memberBaseMemberRepositoryCustom
                .findWithDetailsByIdentifier("manager"))
                .thenReturn(Optional.of(manager));
        when(authManager.getMember()).thenReturn(cashier);
        when(terminalRepository.findAll()).thenReturn(List.of(terminal));
        when(productRepository.findAllById(any()))
                .thenReturn(List.of(product));
        when(invoiceRepository.findLastInvoiceNumberByTerminal(any(), anyBoolean()))
                .thenReturn(null);
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(inv -> {
                    Invoice invoice = inv.getArgument(0);
                    assertEquals(InvoiceStatusType.CANCELLED, invoice.getStatus());
                    return invoice;
                });
        when(terminalRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        orderService.cancelOrder(orderDto, "manager", "test");

        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Cancel order fails if not manager")
    void cancelOrder_notManager() {
        Member user = Member.builder()
                .permissions(Set.of(
                        Permission.builder().permissionType("USER").build()
                ))
                .build();

        when(memberBaseMemberRepositoryCustom
                .findWithDetailsByIdentifier("user"))
                .thenReturn(Optional.of(user));

        assertThrows(ConflictException.class,
                () -> orderService.cancelOrder(orderDto, "user", "reason"));
    }
}
