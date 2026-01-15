package com.ritsard.baisard.domain.member.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to withdraw or cash out from drawer")
public class CashWithdrawRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(description = "Amount of cash to withdraw or cash out", example = "500.00")
    private BigDecimal amount;

    @NotBlank(message = "Manager identifier is required")
    @Schema(description = "Manager's email or identifier for approval", example = "manager@example.com")
    private String managerIdentifier;
}