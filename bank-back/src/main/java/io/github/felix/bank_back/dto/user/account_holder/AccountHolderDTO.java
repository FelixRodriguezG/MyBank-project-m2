package io.github.felix.bank_back.dto.user.account_holder;

import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.model.user.embedded.Address;
import io.github.felix.bank_back.model.user.embedded.PersonalData;
import io.github.felix.bank_back.model.user.enums.Role;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolderDTO {
    @NotNull
    private Long Id;

    @NotBlank
    private String name;

    @NotNull
    private Role role;

    @NotNull
    private UserStatus status;

    @Valid
    @NotNull
    private PersonalData personalData;

    @Valid
    @NotNull
    private Address primaryAddress;

    @Valid
    private Address mailingAddress;

    @NotNull
    private LocalDateTime createdAt;

    public static AccountHolderDTO fromEntity(AccountHolder owner) {
        return new AccountHolderDTO(
                owner.getId(),
                owner.getName(),
                owner.getRole(),
                owner.getStatus(),
                owner.getPersonalData(),
                owner.getPrimaryAddress(),
                owner.getMailingAddress(),
                owner.getCreatedAt()
        );
    }
}
