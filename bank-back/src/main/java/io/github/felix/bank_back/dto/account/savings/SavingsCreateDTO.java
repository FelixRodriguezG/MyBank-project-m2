package io.github.felix.bank_back.dto.account.savings;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SavingsCreateDTO {
    @NotNull
    @PositiveOrZero
    private BigDecimal balanceAmount;

    @NotBlank
    private String currencyCode; // e.g., USD

    @NotBlank
    private String secretKey;

    @NotNull
    private Long primaryOwnerId;

    private Long secondaryOwnerId; // opcional

    // Específico Savings
    @Positive
    private BigDecimal interestRate; // opcional (si null, por defecto)

    @Positive
    private BigDecimal minimumBalance; // opcional (si null, por defecto)
}
