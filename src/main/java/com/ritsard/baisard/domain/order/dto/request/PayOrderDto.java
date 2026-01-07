package com.ritsard.baisard.domain.order.dto.request;

import com.ritsard.baisard.domain.order.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pay Order Request DTO")
public class PayOrderDto {
    @NotNull(message = "Cash tender amount is required")
    @DecimalMin(value = "0.0", message = "Cash tender amount must be non-negative")
    private BigDecimal cashTenderAmount;

    private List<EPaymentDTO> ePayments;

    private DiscountDTO discount;

    @NotEmpty(message = "Items cannot be empty")
    private List<ItemRequestDto> items;
}
