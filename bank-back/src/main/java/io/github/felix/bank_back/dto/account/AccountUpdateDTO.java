package io.github.felix.bank_back.dto.account;

import io.github.felix.bank_back.model.user.embedded.Address;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDTO {
    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private String status; // "ACTIVE" o "FROZEN"

    private Long primaryOwnerId;
    private Long secondaryOwnerId;

    private Address primaryAddress;
    private Address mailingAddress;

    // Campos específicos para cada tipo de cuenta (opcional, según lógica de negocio)
    private BigDecimal minimumBalance;         // Savings, Checking
    private BigDecimal monthlyMaintenanceFee;  // Checking
    private BigDecimal interestRate;           // Savings, CreditCard
    private BigDecimal creditLimit;            // CreditCard
    private BigDecimal penaltyFee;             // Todas

}
