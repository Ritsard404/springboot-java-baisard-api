package com.ritsard.baisard.domain.order.service;

import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.enums.VatType;
import com.ritsard.baisard.domain.inventory.repository.ProductRepository;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.order.dto.request.EPaymentDTO;
import com.ritsard.baisard.domain.order.dto.request.ItemRequestDto;
import com.ritsard.baisard.domain.order.dto.request.OrderDto;
import com.ritsard.baisard.domain.order.entity.EPayment;
import com.ritsard.baisard.domain.order.entity.Invoice;
import com.ritsard.baisard.domain.order.entity.Item;
import com.ritsard.baisard.domain.order.entity.SaleType;
import com.ritsard.baisard.domain.order.entity.enums.InvoiceStatusType;
import com.ritsard.baisard.domain.order.repository.EPaymentRepository;
import com.ritsard.baisard.domain.order.repository.InvoiceRepository;
import com.ritsard.baisard.domain.order.repository.SaleTypeRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.global.utils.Formats;
import com.ritsard.baisard.jwt.repository.member.BaseMemberRepositoryCustom;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final EPaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final SaleTypeRepository saleTypeRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PosTerminalInfoRepository terminalRepository;
    private final AuthManager<Member> authManager;
    private final BaseMemberRepositoryCustom<Member> memberBaseMemberRepositoryCustom;

    @Override
    public void payOrder(OrderDto orderDto) {
        // 1. Validate request
        validateOrderRequest(orderDto);

        // 2. Get current cashier and terminal
        Member cashier = authManager.getMember();
//                .orElseThrow(() -> new NotFoundException("Cashier not found"));

        PosTerminalInfo terminal = getTerminalForUser(cashier);

        // 3. Load products and validate stock
        Map<UUID, Product> productMap = loadAndValidateProducts(orderDto.getItems());

        // 4. Calculate payment details
        PaymentCalculation calculation = calculatePayment(orderDto, productMap, terminal);

        // 5. Validate payment
        validatePayment(calculation, orderDto);

        // 6. Generate invoice number and create invoice
        Long invoiceNumber = generateInvoiceNumber(terminal);
        Invoice invoice = createInvoice(orderDto, cashier, terminal, calculation, invoiceNumber);

        // 7. Create items
        List<Item> items = createItems(orderDto.getItems(), productMap, terminal.isTrainMode());
        invoice.setItems(items);

        // 8. Create e-payments
        if (orderDto.getEPayments() != null && !orderDto.getEPayments().isEmpty()) {
            List<EPayment> ePayments = createEPayments(orderDto.getEPayments(), invoice);
            invoice.setEPayments(ePayments);
        }

        // 9. Deduct stock (only if not in training mode)
        if (!terminal.isTrainMode()) {
            deductStock(orderDto.getItems(), productMap);
        }

        // 10. Save invoice (cascade will save items and payments)
        invoiceRepository.save(invoice);

        // 11. Update terminal counter
        updateTerminalCounter(terminal);

        log.info("Order paid successfully. Invoice number: {} (Train mode: {})",
                invoice.getInvoiceNumber(), terminal.isTrainMode());
    }

    @Override
    public void cancelOrder(OrderDto orderDto, String managerIdentifier, String reason) {
        // 1. Validate manager
        Member manager = memberBaseMemberRepositoryCustom.findWithDetailsByIdentifier(managerIdentifier)
                .orElseThrow(() -> new NotFoundException("Manager not found"));

        if (!hasManagerPrivileges(manager)) {
            throw new ConflictException("User does not have manager privileges");
        }

        // 2. Get current cashier and terminal
        Member cashier = authManager.getMember();
//                .orElseThrow(() -> new NotFoundException("Cashier not found"));

        PosTerminalInfo terminal = getTerminalForUser(cashier);

        // 3. Load products
        Map<UUID, Product> productMap = loadProducts(orderDto.getItems());

        // 4. Calculate payment details (for record keeping)
        PaymentCalculation calculation = calculatePayment(orderDto, productMap, terminal);

        // 5. Generate invoice number and create cancelled invoice
        Long invoiceNumber = generateInvoiceNumber(terminal);
        Invoice invoice = createCancelledInvoice(
                orderDto, cashier, manager, terminal, reason, calculation, invoiceNumber
        );

        // 6. Create items with VOID status
        List<Item> items = createCancelledItems(orderDto.getItems(), productMap, terminal.isTrainMode());
        invoice.setItems(items);

        // 7. Save cancelled invoice
        invoiceRepository.save(invoice);

        // 8. Update terminal counter
        updateTerminalCounter(terminal);

        log.info("Order cancelled successfully. Invoice number: {} by manager: {}",
                invoice.getInvoiceNumber(), manager.getIdentifier());
    }

    // ==================== TERMINAL MANAGEMENT ====================

    private PosTerminalInfo getTerminalForUser(Member member) {
        Company company = member.getCompany();
        if (company == null)
            throw new ConflictException("User has no assigned company");


        // Get the first active terminal for the company
        // In a real scenario, you might want to have a specific terminal assignment per user
        return terminalRepository.findAll().stream()
                .filter(t -> t.getCompany().equals(company))
                .filter(t -> !t.isDeleted())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No active terminal found for user's company"));
    }

    /**
     * Generate invoice number per terminal.
     * Each POS terminal has its own independent sequence.
     * This ensures invoices are unique per terminal like standalone POS systems.
     */
    private synchronized Long generateInvoiceNumber(PosTerminalInfo terminal) {
        boolean isTrainMode = terminal.isTrainMode();

        // Get the last invoice number for THIS SPECIFIC TERMINAL
        Long lastInvoiceNumber = invoiceRepository.findLastInvoiceNumberByTerminal(
                terminal.getUuidPosTerminal(),
                isTrainMode
        );

        // Start from 1 if no previous invoice, otherwise increment
        long nextNumber = (lastInvoiceNumber != null) ? lastInvoiceNumber + 1 : 1;

        // Format based on mode:
        // Train mode: 9000001, 9000002... (easily identifiable as training)
        // Live mode: 1, 2, 3... (normal sequential)
        if (isTrainMode) {
            return 9000000L + nextNumber; // Train mode: 9000001+
        } else {
            return nextNumber; // Live mode: 1, 2, 3...
        }
    }

    private void updateTerminalCounter(PosTerminalInfo terminal) {
        boolean isTrainMode = terminal.isTrainMode();

        if (isTrainMode) {
            terminal.setResetCounterTrainNo(terminal.getResetCounterTrainNo() + 1);
        } else {
            terminal.setResetCounterNo(terminal.getResetCounterNo() + 1);
        }

        terminalRepository.save(terminal);
    }

    // ==================== VALIDATION METHODS ====================

    private void validateOrderRequest(OrderDto orderDto) {
        if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            throw new ConflictException("Items cannot be empty");
        }

        if (orderDto.getCashTenderAmount() == null) {
            throw new ConflictException("Cash tender amount is required");
        }

        // Validate each item
        for (ItemRequestDto item : orderDto.getItems()) {
            if (item.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ConflictException("Quantity must be greater than zero");
            }
            if (item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ConflictException("Price cannot be negative");
            }
        }
    }

    private void validatePayment(PaymentCalculation calc, OrderDto orderDto) {
        if (calc.getCashTendered().compareTo(calc.getTotalAmount()) < 0) {
            throw new ConflictException(
                    String.format("Insufficient payment. Required: %s, Tendered: %s",
                            Formats.pesoFormat(calc.getTotalAmount()),
                            Formats.pesoFormat(calc.getCashTendered()))
            );
        }
    }

    private boolean hasManagerPrivileges(Member member) {
        return member.getPermissions().stream()
                .anyMatch(p -> p.getPermissionType().equalsIgnoreCase("ADMIN") ||
                        p.getPermissionType().equalsIgnoreCase("MANAGER") ||
                        p.getPermissionType().equalsIgnoreCase("SUPERADMIN"));
    }

    // ==================== PRODUCT LOADING AND VALIDATION ====================

    private Map<UUID, Product> loadAndValidateProducts(List<ItemRequestDto> items) {
        Map<UUID, Product> productMap = loadProducts(items);

        // Validate stock availability
        for (ItemRequestDto item : items) {
            Product product = productMap.get(item.getUuidProduct());

            if (product.getQuantity() == null ||
                    product.getQuantity().compareTo(item.getQty()) < 0) {
                throw new ConflictException(
                        String.format("Insufficient stock for product: %s. Available: %s, Required: %s",
                                product.getName(),
                                product.getQuantity(),
                                item.getQty())
                );
            }
        }

        return productMap;
    }

    private Map<UUID, Product> loadProducts(List<ItemRequestDto> items) {
        List<UUID> productIds = items.stream()
                .map(ItemRequestDto::getUuidProduct)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new NotFoundException("One or more products not found");
        }

        return products.stream()
                .collect(Collectors.toMap(Product::getUuidProduct, p -> p));
    }

    // ==================== PAYMENT CALCULATION ====================

    private PaymentCalculation calculatePayment(
            OrderDto orderDto,
            Map<UUID, Product> productMap,
            PosTerminalInfo terminal
    ) {
        // Get VAT configuration from terminal
        BigDecimal vatRate = BigDecimal.valueOf(terminal.getVat())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP); // Convert 12 to 0.12
        BigDecimal maxDiscount = terminal.getDiscountMax();

        // Calculate VAT breakdown by VAT type
        BigDecimal vatableTotal = calculateTotalByVatType(orderDto.getItems(), productMap, VatType.VATABLE);
        BigDecimal vatExemptTotal = calculateTotalByVatType(orderDto.getItems(), productMap, VatType.EXEMPT);
        BigDecimal vatZeroTotal = calculateTotalByVatType(orderDto.getItems(), productMap, VatType.ZERO);

        // Calculate VAT Sales and VAT Amount
        BigDecimal vatSales = vatableTotal.divide(
                BigDecimal.ONE.add(vatRate),
                2,
                RoundingMode.HALF_UP
        );
        BigDecimal vatAmount = vatableTotal.subtract(vatSales);

        // Calculate gross total
        BigDecimal grossTotal = orderDto.getItems().stream()
                .map(ItemRequestDto::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate discount
        BigDecimal discountAmount = calculateDiscountAmount(orderDto.getDiscount(), grossTotal, maxDiscount);

        // Calculate totals
        BigDecimal totalAmount = grossTotal.subtract(discountAmount);
        BigDecimal amountDue = totalAmount;
        BigDecimal subTotal = amountDue.subtract(vatAmount);

        // Calculate tender
        BigDecimal ePaymentTotal = orderDto.getEPayments() != null
                ? orderDto.getEPayments().stream()
                .map(EPaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        // Limit ePayment to remaining balance after cash
        BigDecimal remainingAfterCash = totalAmount.subtract(orderDto.getCashTenderAmount());
        BigDecimal effectiveEPayment = ePaymentTotal.min(remainingAfterCash.max(BigDecimal.ZERO));

        BigDecimal tenderAmount = orderDto.getCashTenderAmount().add(effectiveEPayment);
        BigDecimal changeAmount = tenderAmount.subtract(totalAmount);

        return PaymentCalculation.builder()
                .grossAmount(grossTotal)
                .totalAmount(totalAmount)
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .vatableTotal(vatableTotal)
                .vatSales(vatSales)
                .vatAmount(vatAmount)
                .vatExempt(vatExemptTotal)
                .vatZero(vatZeroTotal)
                .cashTendered(orderDto.getCashTenderAmount())
                .totalTendered(tenderAmount)
                .changeAmount(changeAmount)
                .dueAmount(amountDue)
                .build();
    }

    private BigDecimal calculateTotalByVatType(
            List<ItemRequestDto> items,
            Map<UUID, Product> productMap,
            VatType vatType
    ) {
        return items.stream()
                .filter(item -> {
                    Product product = productMap.get(item.getUuidProduct());
                    return product != null && product.getVatType() == vatType;
                })
                .map(ItemRequestDto::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscountAmount(
            com.ritsard.baisard.domain.order.dto.request.DiscountDTO discount,
            BigDecimal grossTotal,
            BigDecimal maxDiscount
    ) {
        if (discount == null) {
            return BigDecimal.ZERO;
        }

        // Fixed amount discount
        if (discount.getDiscountAmount() != null &&
                discount.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            return discount.getDiscountAmount().min(maxDiscount);
        }

        // Percentage discount
        if (discount.getDiscountPercent() != null && discount.getDiscountPercent() > 0) {
            BigDecimal percent = BigDecimal.valueOf(discount.getDiscountPercent());
            BigDecimal amount = grossTotal.multiply(percent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return amount.min(maxDiscount);
        }

        return BigDecimal.ZERO;
    }

    // ==================== INVOICE CREATION ====================

    private Invoice createInvoice(
            OrderDto orderDto,
            Member cashier,
            PosTerminalInfo terminal,
            PaymentCalculation calc,
            Long invoiceNumber
    ) {
        Invoice.InvoiceBuilder builder = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .cashier(cashier)
                .grossAmount(calc.getGrossAmount())
                .totalAmount(calc.getTotalAmount())
                .subTotal(calc.getSubTotal())
                .cashTendered(calc.getCashTendered())
                .dueAmount(calc.getDueAmount())
                .totalTendered(calc.getTotalTendered())
                .changeAmount(calc.getChangeAmount())
                .vatSales(calc.getVatSales())
                .vatExempt(calc.getVatExempt())
                .vatAmount(calc.getVatAmount())
                .vatZero(calc.getVatZero())
                .discountAmount(calc.getDiscountAmount())
                .status(InvoiceStatusType.PAID)
                .isTrainMode(terminal.isTrainMode());

        // Add discount information if present
        if (orderDto.getDiscount() != null) {
            builder.eligibleDiscName(orderDto.getDiscount().getEligibleDiscName())
                    .oscaIdNum(orderDto.getDiscount().getOscaIdNum())
                    .discountType(orderDto.getDiscount().getDiscountType() != null
                            ? orderDto.getDiscount().getDiscountType().getDescription()
                            : null)
                    .discountPercent(orderDto.getDiscount().getDiscountPercent());
        }

        return builder.build();
    }

    private Invoice createCancelledInvoice(
            OrderDto orderDto,
            Member cashier,
            Member manager,
            PosTerminalInfo terminal,
            String reason,
            PaymentCalculation calc,
            Long invoiceNumber
    ) {
        return Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .cashier(cashier)
                .voidedBy(manager)
                .reason(reason)
                .grossAmount(calc.getGrossAmount())
                .totalAmount(BigDecimal.ZERO) // Cancelled = 0
                .subTotal(BigDecimal.ZERO)
                .cashTendered(BigDecimal.ZERO)
                .dueAmount(BigDecimal.ZERO)
                .totalTendered(BigDecimal.ZERO)
                .changeAmount(BigDecimal.ZERO)
                .vatSales(BigDecimal.ZERO)
                .vatExempt(BigDecimal.ZERO)
                .vatAmount(BigDecimal.ZERO)
                .vatZero(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .status(InvoiceStatusType.CANCELLED)
                .isTrainMode(terminal.isTrainMode())
                .build();
    }

    // ==================== ITEM CREATION ====================

    private List<Item> createItems(
            List<ItemRequestDto> itemDtos,
            Map<UUID, Product> productMap,
            boolean isTrainMode
    ) {
        List<Item> items = new ArrayList<>();

        for (ItemRequestDto dto : itemDtos) {
            Product product = productMap.get(dto.getUuidProduct());

            Item item = Item.builder()
                    .qty(dto.getQty())
                    .price(dto.getPrice())
                    .subTotal(dto.getSubTotal())
                    .status(InvoiceStatusType.PAID)
                    .product(product)
                    .isTrainingMode(isTrainMode)
                    .build();

            items.add(item);
        }

        return items;
    }

    private List<Item> createCancelledItems(
            List<ItemRequestDto> itemDtos,
            Map<UUID, Product> productMap,
            boolean isTrainMode
    ) {
        List<Item> items = new ArrayList<>();

        for (ItemRequestDto dto : itemDtos) {
            Product product = productMap.get(dto.getUuidProduct());

            Item item = Item.builder()
                    .qty(dto.getQty())
                    .price(dto.getPrice())
                    .subTotal(BigDecimal.ZERO) // Cancelled items have 0 subtotal
                    .status(InvoiceStatusType.VOID)
                    .product(product)
                    .isTrainingMode(isTrainMode)
                    .build();

            items.add(item);
        }

        return items;
    }

    // ==================== E-PAYMENT CREATION ====================

    private List<EPayment> createEPayments(List<EPaymentDTO> paymentDtos, Invoice invoice) {
        List<EPayment> payments = new ArrayList<>();

        for (EPaymentDTO dto : paymentDtos) {
            SaleType saleType = saleTypeRepository.findById(dto.getUuidSaleType())
                    .orElseThrow(() -> new NotFoundException("Sale type not found: " + dto.getUuidSaleType()));

            EPayment payment = EPayment.builder()
                    .reference(dto.getReference())
                    .amount(dto.getAmount())
                    .invoice(invoice)
                    .saleType(saleType)
                    .build();

            payments.add(payment);
        }

        return payments;
    }

    // ==================== STOCK DEDUCTION ====================

    private void deductStock(List<ItemRequestDto> items, Map<UUID, Product> productMap) {
        for (ItemRequestDto item : items) {
            Product product = productMap.get(item.getUuidProduct());

            // Deduct quantity
            BigDecimal newQuantity = product.getQuantity().subtract(item.getQty());
            product.setQuantity(newQuantity);

            // Update product
            productRepository.save(product);
        }
    }

    // ==================== INNER CLASS FOR CALCULATION ====================

    @lombok.Data
    @lombok.Builder
    private static class PaymentCalculation {
        private BigDecimal grossAmount;
        private BigDecimal totalAmount;
        private BigDecimal subTotal;
        private BigDecimal discountAmount;
        private BigDecimal vatableTotal;
        private BigDecimal vatSales;
        private BigDecimal vatAmount;
        private BigDecimal vatExempt;
        private BigDecimal vatZero;
        private BigDecimal cashTendered;
        private BigDecimal totalTendered;
        private BigDecimal changeAmount;
        private BigDecimal dueAmount;
    }
}