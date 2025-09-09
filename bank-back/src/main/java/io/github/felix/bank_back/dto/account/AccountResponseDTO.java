package io.github.felix.bank_back.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Long id;
    private BigDecimal balance;
    private LocalDate creationDate;
    private AccountStatus status;
    private AccountType accountType;
    private BigDecimal penaltyFee;
    private AccountHolderDTO primaryOwner;
    private AccountHolderDTO secondaryOwner;
}