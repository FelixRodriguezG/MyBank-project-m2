package io.github.felix.bank_back.dto.account.credit_card;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreditCardResponseDTO extends AccountResponseDTO {
    @NotNull
    @Positive
    private BigDecimal creditLimit;

    @NotNull
    @Positive
    private BigDecimal interestRate;
}
