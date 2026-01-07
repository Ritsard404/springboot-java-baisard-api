package com.ritsard.baisard.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "E-Payment Request DTO")
public class EPaymentDTO {
    @NotBlank(message = "Reference is required")
    private String reference;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Sale type UUID is required")
    private UUID uuidSaleType;

    @NotBlank(message = "Sale type name is required")
    private String saleTypeName;
}
