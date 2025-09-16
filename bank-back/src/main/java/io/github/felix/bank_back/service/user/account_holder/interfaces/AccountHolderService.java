package io.github.felix.bank_back.service.user.account_holder.interfaces;

import io.github.felix.bank_back.dto.user.account_holder.AccountHolderCreateDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface AccountHolderService {
    AccountHolderDTO createAccountHolder(AccountHolderCreateDTO accountHolder);
    AccountHolderDTO getAccountHolderById(Long id);
    List<AccountHolderDTO> getAllAccountHolders();
    List<AccountHolderDTO> findByFirstName(String firstName);
    List<AccountHolderDTO> findByLastName(String lastName);
    List<AccountHolderDTO> findByStatus(UserStatus status);
    List<AccountHolderDTO> findByDateOfBirth(LocalDate dateOfBirth);
    List<AccountHolderDTO> findByPrimaryCity(String city);
    List<AccountHolderDTO> findByMailingCity(String city);
    AccountHolderDTO updateAccountHolder(Long id, AccountHolderCreateDTO updatedData);
    void deleteAccountHolder(Long id);
    Page<AccountHolderDTO> findByStatus(UserStatus status, Pageable pageable);
}
