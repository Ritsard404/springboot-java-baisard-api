package com.ritsard.baisard.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object representing an invoice with complete business and transaction details.
 * This DTO is used for invoice reporting and receipt generation in the POS system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Invoice details including business information, items, and payment details")
public class InvoiceDTO {

    @Valid
    @NotNull(message = "Business details are required")
    @JsonProperty("businesDetails")
    @Schema(description = "Business and POS machine details")
    private BusinessDetails businessDetails;

    // Invoice Details
    @NotBlank(message = "Invoice number is required")
    @JsonProperty("invoiceNum")
    @Schema(description = "Unique invoice number", example = "INV-2024-001234")
    private String invoiceNum;

    @NotBlank(message = "Invoice date is required")
    @JsonProperty("invoiceDate")
    @Schema(description = "Date when the invoice was issued", example = "2024-01-07")
    private String invoiceDate;

    @NotBlank(message = "Cashier name is required")
    @JsonProperty("cashierName")
    @Schema(description = "Name of the cashier who processed the transaction", example = "John Doe")
    private String cashierName;

    // Items
    @Valid
    @Builder.Default
    @JsonProperty("items")
    @Schema(description = "List of items purchased in this invoice")
    private List<ItemInfo> items = new ArrayList<>();

    // Totals
    @NotBlank(message = "Total amount is required")
    @JsonProperty("totalAmount")
    @Schema(description = "Total amount before discounts", example = "1500.00")
    private String totalAmount;

    @NotBlank(message = "Discount amount is required")
    @JsonProperty("discountAmount")
    @Schema(description = "Total discount applied", example = "150.00")
    private String discountAmount;

    @NotBlank(message = "Subtotal is required")
    @JsonProperty("subTotal")
    @Schema(description = "Subtotal after discounts", example = "1350.00")
    private String subTotal;

    @NotBlank(message = "Due amount is required")
    @JsonProperty("dueAmount")
    @Schema(description = "Amount due for payment", example = "1350.00")
    private String dueAmount;

    @Valid
    @Builder.Default
    @JsonProperty("otherPayments")
    @Schema(description = "List of non-cash payment methods used")
    private List<OtherPayment> otherPayments = new ArrayList<>();

    @NotBlank(message = "Cash tender amount is required")
    @JsonProperty("cashTenderAmount")
    @Schema(description = "Amount of cash tendered by customer", example = "1500.00")
    private String cashTenderAmount;

    @NotBlank(message = "Total tender amount is required")
    @JsonProperty("totalTenderAmount")
    @Schema(description = "Total amount tendered (cash + other payments)", example = "1500.00")
    private String totalTenderAmount;

    @NotBlank(message = "Change amount is required")
    @JsonProperty("changeAmount")
    @Schema(description = "Change to be returned to customer", example = "150.00")
    private String changeAmount;

    @NotBlank(message = "VAT exempt sales is required")
    @JsonProperty("vatExemptSales")
    @Schema(description = "Total sales exempt from VAT", example = "0.00")
    private String vatExemptSales;

    @NotBlank(message = "VAT sales is required")
    @JsonProperty("vatSales")
    @Schema(description = "Total taxable sales (VAT-inclusive)", example = "1203.57")
    private String vatSales;

    @NotBlank(message = "VAT amount is required")
    @JsonProperty("vatAmount")
    @Schema(description = "Total VAT amount charged", example = "146.43")
    private String vatAmount;

    @NotBlank(message = "VAT zero is required")
    @JsonProperty("vatZero")
    @Schema(description = "Total zero-rated sales", example = "0.00")
    private String vatZero;

    @JsonProperty("elligiblePersonDiscount")
    @Schema(description = "Discount for eligible persons (senior citizens, PWD, etc.)", example = "150.00")
    private String eligiblePersonDiscount;

    @JsonProperty("discountType")
    @Schema(description = "Type of discount applied", example = "Senior Citizen Discount")
    private String discountType;

    @Builder.Default
    @JsonProperty("isReturned")
    @Schema(description = "Indicates if this invoice has been returned/refunded", example = "false")
    private boolean isReturned = false;

    /**
     * Business and POS machine details required for invoice compliance.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Business registration and POS machine accreditation details")
    public static class BusinessDetails {

        @NotBlank(message = "POS serial number is required")
        @JsonProperty("posSerialNumber")
        @Schema(description = "Serial number of the POS machine", example = "SN123456789")
        private String posSerialNumber;

        @NotBlank(message = "MIN number is required")
        @JsonProperty("minNumber")
        @Schema(description = "Machine Identifier Number of the POS machine", example = "MIN123456789")
        private String minNumber;

        @NotBlank(message = "Accreditation number is required")
        @JsonProperty("accreditationNumber")
        @Schema(description = "BIR accreditation number of the POS machine", example = "ACC123456789")
        private String accreditationNumber;

        @NotBlank(message = "PTU number is required")
        @JsonProperty("ptuNumber")
        @Schema(description = "Point of Transaction Unit number", example = "PTU123456789")
        private String ptuNumber;

        @NotBlank(message = "Date issued is required")
        @JsonProperty("dateIssued")
        @Schema(description = "Date when the POS machine accreditation was issued", example = "2023-01-01")
        private String dateIssued;

        @NotBlank(message = "Valid until date is required")
        @JsonProperty("validUntil")
        @Schema(description = "Date until the POS machine accreditation is valid", example = "2025-12-31")
        private String validUntil;

        @NotBlank(message = "POS name is required")
        @JsonProperty("posName")
        @Schema(description = "Name of the POS system or terminal", example = "Main Counter POS")
        private String posName;

        @NotBlank(message = "Registered name is required")
        @JsonProperty("registeredName")
        @Schema(description = "Official registered name of the business", example = "ABC Retail Corporation")
        private String registeredName;

        @NotBlank(message = "Operated by is required")
        @JsonProperty("operatedBy")
        @Schema(description = "Name of the business operator", example = "ABC Retail Corporation")
        private String operatedBy;

        @NotBlank(message = "Address is required")
        @JsonProperty("address")
        @Schema(description = "Physical address of the business", example = "123 Main St, Quezon City, Metro Manila")
        private String address;

        @NotBlank(message = "VAT TIN number is required")
        @JsonProperty("vatTinNumber")
        @Schema(description = "VAT Tax Identification Number of the business", example = "123-456-789-000")
        private String vatTinNumber;

        @NotBlank(message = "Cost center is required")
        @JsonProperty("costCenter")
        @Schema(description = "Cost center code for accounting purposes", example = "CC001")
        private String costCenter;

        @NotBlank(message = "Branch center is required")
        @JsonProperty("branchCenter")
        @Schema(description = "Branch identifier code", example = "BR001")
        private String branchCenter;

        @Builder.Default
        @JsonProperty("isTrainMode")
        @Schema(description = "Indicates if the POS is in training mode", example = "false")
        private boolean isTrainMode = false;
    }

    /**
     * Individual item information in the invoice.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual item details in the invoice")
    public static class ItemInfo {

        @NotBlank(message = "Quantity is required")
        @JsonProperty("qty")
        @Schema(description = "Quantity of items purchased", example = "2")
        private String qty;

        @NotBlank(message = "Description is required")
        @JsonProperty("description")
        @Schema(description = "Item description or name", example = "Product ABC - 500ml")
        private String description;

        @NotBlank(message = "Amount is required")
        @JsonProperty("amount")
        @Schema(description = "Total amount for this line item", example = "250.00")
        private String amount;
    }

    /**
     * Non-cash payment method details.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Details of non-cash payment methods used")
    public static class OtherPayment {

        @NotBlank(message = "Sale type name is required")
        @JsonProperty("saleTypeName")
        @Schema(description = "Type of payment method", example = "Credit Card")
        private String saleTypeName;

        @NotBlank(message = "Reference is required")
        @JsonProperty("reference")
        @Schema(description = "Payment reference number or transaction ID", example = "REF123456789")
        private String reference;

        @NotBlank(message = "Amount is required")
        @JsonProperty("amount")
        @Schema(description = "Amount paid using this payment method", example = "500.00")
        private String amount;
    }
}
