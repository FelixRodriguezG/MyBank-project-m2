package io.github.felix.bank_back.dto.account.credit_card;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCardCreateDTO {
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

    // Específico CreditCard
    @Positive
    private BigDecimal creditLimit; // opcional (si null, por defecto)

    @Positive
    private BigDecimal interestRate; // opcional (si null, por defecto)
}
