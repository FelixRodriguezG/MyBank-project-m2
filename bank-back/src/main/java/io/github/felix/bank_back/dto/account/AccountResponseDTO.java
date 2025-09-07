package io.github.felix.bank_back.dto.account;


import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.dto.user.AccountHolderDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Long id;
    private Money balance;
    private String secretKey;
    private LocalDate creationDate;
    private AccountStatus status;
    private Money penaltyFee;
    private AccountHolderDTO primaryOwner;
    private AccountHolderDTO secondaryOwner;
}
