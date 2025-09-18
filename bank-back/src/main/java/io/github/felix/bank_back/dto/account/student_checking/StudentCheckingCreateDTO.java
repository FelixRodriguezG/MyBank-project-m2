package io.github.felix.bank_back.dto.account.student_checking;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentCheckingCreateDTO {
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
}
