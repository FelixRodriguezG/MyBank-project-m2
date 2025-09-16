package io.github.felix.bank_back.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    @NotNull
    private Long id;

    private BigDecimal balance;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate creationDate;

    @NotNull
    private AccountStatus status;

    @NotNull
    private AccountType accountType;

    @NotNull
    private BigDecimal penaltyFee;

    @NotNull
    @Valid
    private AccountHolderDTO primaryOwner;

    @Valid
    private AccountHolderDTO secondaryOwner;
}