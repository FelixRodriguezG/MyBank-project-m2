package io.github.felix.bank_back.dto.user.account_holder;

import io.github.felix.bank_back.model.user.embedded.Address;
import io.github.felix.bank_back.model.user.embedded.PersonalData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de creación de AccountHolder.
 */
@Data
public class AccountHolderCreateDTO {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private PersonalData personalData;

    @NotNull
    private Address primaryAddress;

    private Address mailingAddress; // opcional
}
