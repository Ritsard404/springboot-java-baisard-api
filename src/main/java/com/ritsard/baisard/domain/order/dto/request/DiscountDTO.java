package com.ritsard.baisard.domain.order.dto.request;

import com.ritsard.baisard.domain.order.entity.enums.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Discount Request DTO")
public class DiscountDTO {
    private String eligibleDiscName;
    private String oscaIdNum;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @Min(value = 0, message = "Discount percent must be non-negative")
    @Max(value = 100, message = "Discount percent cannot exceed 100")
    private Integer discountPercent;

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount;
}
