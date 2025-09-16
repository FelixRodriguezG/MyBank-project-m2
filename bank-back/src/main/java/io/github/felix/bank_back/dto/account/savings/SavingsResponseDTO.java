package io.github.felix.bank_back.dto.account.savings;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SavingsResponseDTO extends AccountResponseDTO {
    @NotNull
    @Positive
    private BigDecimal minimumBalance;

    @NotNull
    @Positive
    private BigDecimal interestRate;
}
