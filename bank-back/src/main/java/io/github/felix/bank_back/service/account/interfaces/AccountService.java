package io.github.felix.bank_back.service.account.interfaces;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;

import java.util.List;

public interface AccountService {

    List<AccountResponseDTO> getAccountsByPrimaryOwnerAndSecondaryOwner(Long primaryOwnerId, Long secondaryOwnerId);

    List<AccountResponseDTO> getAccountsByPrimaryOwnerId(Long primaryOwnerId);

    List<AccountResponseDTO> getAccountsBySecondaryOwnerId(Long secondaryOwnerId);

    List<AccountResponseDTO> getAccountsByStatus(AccountStatus status);

    List<AccountResponseDTO> getAccountsByType(AccountType accountType);

    List<AccountResponseDTO> applyPenaltyToAccountsWithLowBalance();

    List<AccountResponseDTO> applyPenaltyToStudentAccountsWithNegativeBalance();

    List<AccountResponseDTO> getCheckingAccountsWithMaintenanceFeeDue();

    List<AccountResponseDTO> getSavingsAccountsWithInterestDue();

    List<AccountResponseDTO> getCreditCardAccountsWithInterestDue();

    boolean deleteAccountById(Long accountId);

}
