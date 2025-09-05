package io.github.felix.bank_back.model.user;

import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.embedded.Address;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AccountHolder extends Account {
    @NotNull
    private String name;

    @NotNull
    private String dateOfBirth;

    @NotNull
    private String primaryAddress;

    @NotNull
    private Address mailingAddress;
}